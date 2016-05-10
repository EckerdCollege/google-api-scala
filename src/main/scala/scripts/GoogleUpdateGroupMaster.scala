package scripts


import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl
import google.services.admin.directory.Directory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import persistence.entities.representations.GroupMaster_R
import persistence.entities.tables.{GROUP_MASTER, GWBALIAS}

import scala.util.Try

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupMaster {

  def update(implicit service: Directory) = {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modules.dbConfig.driver.api._
    val db = modules.db
    val GROUP_MASTER_TABLEQUERY = TableQuery[GROUP_MASTER]
    val GWBALIAS = TableQuery[GWBALIAS]

    Await.result(db.run(GROUP_MASTER_TABLEQUERY.schema.create), Duration.Inf)

    val currentGroupsinGoogle = service.groups.list()

    val existsTupleFuture = Future.sequence(
      currentGroupsinGoogle.par.map{ group =>
        db.run(GROUP_MASTER_TABLEQUERY.withFilter(a => a.id === group.id.get).result)
          .map(matchingRecs => (group, matchingRecs.headOption))
    }.seq)

    val withGwbaliasrecords = existsTupleFuture.flatMap {
        tuples =>
        val seqOfFutures = tuples.map {  tuple =>
          val future = db.run( GWBALIAS.withFilter(rec =>
            rec.alias.toUpperCase === tuple._1.email.replace("@eckerd.edu", "").toUpperCase).result
          )
            .map(_.headOption)
            .map{ record =>
            GroupMaster_R(
              tuple._1.id.get,
              tuple._2 match {
                case None => record match {
                  case Some(rec) => "Y"
                  case None => "N"
                }
                case Some(rec) => rec.autoIndicator
              },
              tuple._1.name,
              tuple._1.email,
              tuple._1.directMemberCount.get,
              Option(tuple._1.description.get.take(254)),
              tuple._2.flatMap(rec => rec.processIndicator),
              tuple._2 match {
                case Some(rec) => rec.autoType
                case None =>
                  val result = record match {
                    case Some(recordType) => Some(recordType.typePkCk)
                    case None => None
                  }
                  result
              },
              tuple._2 match {
                case Some(rec) => rec.autoKey
                case None =>
                  val result = record match {
                    case Some(recordType) => Some(recordType.keyPk)
                    case None => None
                  }
                  result
              },
              tuple._2 match {
                case Some(rec) => rec.autoTermCode
                case None =>
                  val result = record match {
                    case Some(recordType) => Some(recordType.termCode)
                    case None => None
                  }
                  result
              }
            )
          }
          future
      }
          Future.sequence(seqOfFutures)
    }


    val currentGroups = Await.result(withGwbaliasrecords, Duration.Inf)

    val updated = Future.sequence(currentGroups.map(group =>
      db.run(GROUP_MASTER_TABLEQUERY.insertOrUpdate(group))))
    Await.result(updated, Duration.Inf)
  }
}
