package scripts

import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import GoogleAdmin.listAllGroups
import persistence.entities.representations.GroupMaster_R
import persistence.entities.tables.GROUP_MASTER

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupMaster extends App {
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._
  val db = modules.db

  val GROUP_MASTER_TABLEQUERY = TableQuery[GROUP_MASTER]

  Await.result(db.run(GROUP_MASTER_TABLEQUERY.schema.create ), Duration.Inf)

  val groups = listAllGroups()

  val existsTupleFuture = Future.sequence( groups.par.map(group =>  db.run(GROUP_MASTER_TABLEQUERY.withFilter(a => a.id === group.getId).result).map(a => (group, a.headOption))).seq)

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
          Option(tuple._1.getDescription.take(254))
        )
    )
  )

  val currentGroups = Await.result(FutureMadness, Duration.Inf)

  val whatIsThis = Future.sequence( currentGroups.map( group =>   db.run(GROUP_MASTER_TABLEQUERY.insertOrUpdate(group))))
  Await.result(whatIsThis, Duration.Inf)

}
