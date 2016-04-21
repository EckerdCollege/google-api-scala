package scripts

import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import java.sql.Timestamp
import GoogleAdmin.listAllGroups

/**
  * Created by davenpcm on 4/21/2016.
  */
object GoogleUpdateGroupMaster extends App {
  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  import modules.dbConfig.driver.api._

  val db = modules.db

  case class GroupIdent(id: String,
                        name: String,
                        email: String,
                        count: Long,
                        desc: Option[String]
                       )



  class GROUP_MASTER(tag: Tag) extends Table[GroupIdent](tag, "GROUP_MASTER"){
    def id = column[String]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def email = column[String]("EMAIL")
    def count = column[Long]("MEMBER_COUNT")
    def desc = column[Option[String]]("DESCRIPTION")



    def * = (id, name, email, count, desc) <> (GroupIdent.tupled, GroupIdent.unapply)
  }

  val GROUP_MASTER_TABLEQUERY = TableQuery[GROUP_MASTER]

//  Await.result(db.run(GROUP_MASTER_TABLEQUERY.schema.create ), Duration.Inf)


  val groups = listAllGroups()

  val groupidents = groups.map(group =>
    GroupIdent( group.getId,
      group.getName,
      group.getEmail,
      group.getDirectMembersCount,
      Option(group.getDescription.take(254))
    )
  )



  val whatIsThis = Future.sequence( groupidents.map(group =>   db.run(GROUP_MASTER_TABLEQUERY.insertOrUpdate(group))))
  Await.result(whatIsThis, Duration.Inf)




}
