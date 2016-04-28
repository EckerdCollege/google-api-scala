package scripts

import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import GoogleAdmin.{listAllGroups, listAllGroupMembers}

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupToIdent extends App {
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._

  val db = modules.db

  case class GroupIdent(id: String, name: String, email: String, count: Long, desc: String)
  case class Group2Ident(
                          groupId: String,
                          identID: String,
                          autoIndicator: String,
                          memberRole: String,
                          memberType: String
                        )

  class GROUPTOIDENT(tag: Tag) extends Table[Group2Ident](tag, "GROUP_TO_IDENT") {
    def groupId = column[String]("GROUP_ID")
    def identID = column[String]("IDENT_ID")
    def autoIndicator = column[String]("AUTO_INDICATOR")
    def memberRole = column[String]("MEMBER_ROLE")
    def memberType = column[String]("MEMBER_TYPE")

    def * = (groupId, identID, autoIndicator, memberRole, memberType) <> (Group2Ident.tupled, Group2Ident.unapply )
  }
  
  val group2IdentTableQuery = TableQuery[GROUPTOIDENT]
  Await.result(db.run( group2IdentTableQuery.schema.create), Duration.Inf)

  val groups = listAllGroups()

  val groupidents = groups.map(group =>
    GroupIdent( group.getId, group.getName, group.getEmail, group.getDirectMembersCount, group.getDescription)
  )

  val group2Members = groupidents.par.map(ident => listAllGroupMembers(ident.email).map(member => Group2Ident(ident.id, member.getId,"N", member.getRole, member.getType)))
    .foldLeft(List[Group2Ident]())((acc, next) => next ::: acc)

//  val group2Members = List(Group2Ident("test", "test"))


  val whatIsThis = db.run(group2IdentTableQuery ++= group2Members )
  Await.result(whatIsThis, Duration.Inf)
}
