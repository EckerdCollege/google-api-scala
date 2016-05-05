package scripts


import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import google.services.admin.directory.Directory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import persistence.entities.representations.GroupMaster_R
import persistence.entities.tables.GROUP_MASTER

import scala.util.Try

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupMaster extends App {

  def update(service: Directory) = {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modules.dbConfig.driver.api._
    val db = modules.db
    val GROUP_MASTER_TABLEQUERY = TableQuery[GROUP_MASTER]


    // Create Table or Silently Fail
    Try(Await.result(db.run(GROUP_MASTER_TABLEQUERY.schema.create), Duration.Inf))

    val currentGroupsinGoogle = service.groups.list()

    val existsTupleFuture = Future.sequence(currentGroupsinGoogle.par.map(group =>
      db.run(GROUP_MASTER_TABLEQUERY.withFilter(a => a.id === group.getId).result)
        .map(matchingRecs => (group, matchingRecs.headOption))).seq)

    val FutureMadness = existsTupleFuture.map(
      tuples => tuples.map(
        tuple =>
          GroupMaster_R(
            tuple._1.getId,
            tuple._2 match {
              case None => "N"
              case Some(rec) => rec.autoIndicator
            },
            tuple._1.getName,
            tuple._1.getEmail,
            tuple._1.getDirectMembersCount,
            Option(tuple._1.getDescription.take(254)),
            tuple._2.flatMap(rec => rec.processIndicator)
          )
      )
    )

    val currentGroups = Await.result(FutureMadness, Duration.Inf)

    val updated = Future.sequence(currentGroups.map(group =>
      db.run(GROUP_MASTER_TABLEQUERY.insertOrUpdate(group))))
    Await.result(updated, Duration.Inf)
  }
}
