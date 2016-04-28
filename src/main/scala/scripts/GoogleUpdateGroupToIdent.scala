package scripts

import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import GoogleAdmin.{listAllGroupMembers, listAllGroups}
import persistence.entities.representations.Group2Ident_R
import persistence.entities.tables.GROUPTOIDENT

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupToIdent extends App {
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._
  val db = modules.db

  case class GroupIdent(id: String, name: String, email: String, count: Long, desc: String)

  val group2IdentTableQuery = TableQuery[GROUPTOIDENT]
//  Await.result(db.run( group2IdentTableQuery.schema.create), Duration.Inf)

  val groups = listAllGroups()

  val groupidents = groups.map(group =>
    GroupIdent( group.getId, group.getName, group.getEmail, group.getDirectMembersCount, group.getDescription)
  )

  val group2Members = groupidents.par.map(ident => listAllGroupMembers(ident.email).map(member => Group2Ident_R(ident.id, member.getId,"N", member.getRole, member.getType)))
    .foldLeft(List[Group2Ident_R]())((acc, next) => next ::: acc)

  val whatIsThis = db.run(group2IdentTableQuery ++= group2Members )
  Await.result(whatIsThis, Duration.Inf)
}
