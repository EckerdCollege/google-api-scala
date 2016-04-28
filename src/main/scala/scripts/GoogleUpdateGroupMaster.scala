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

  val groupidents = groups.map(group =>
    GroupMaster_R( group.getId,
      "N",
      group.getName,
      group.getEmail,
      group.getDirectMembersCount,
      Option(group.getDescription.take(254))
    )
  )

  val whatIsThis = Future.sequence( groupidents.map(group =>   db.run(GROUP_MASTER_TABLEQUERY.insertOrUpdate(group))))
  Await.result(whatIsThis, Duration.Inf)

}
