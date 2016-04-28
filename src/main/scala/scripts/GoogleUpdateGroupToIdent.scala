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
  Await.result(db.run( group2IdentTableQuery.schema.create), Duration.Inf)

  val groups = listAllGroups()

  val groupidents = groups.map(group =>
    GroupIdent( group.getId, group.getName, group.getEmail, group.getDirectMembersCount, group.getDescription)
  )

  val group2Members = groupidents.par.map(ident =>
    listAllGroupMembers(ident.email)
    .map(member =>
      (Group2Ident_R(ident.id, member.getId,"", member.getRole, member.getType),
        Await.result(db.run(group2IdentTableQuery.withFilter(rec =>
          rec.groupId === ident.id && rec.identID === member.getId).result.headOption), Duration(1, "second"))))
  )

  val Tuples = group2Members
    .foldLeft(List[(Group2Ident_R, Option[Group2Ident_R])]())((acc, next) => next ::: acc)

  val idents = Tuples.map( tuple =>
    Group2Ident_R(
      tuple._1.groupId,
      tuple._1.identID,
      tuple._2 match {
        case None => "N"
        case Some(rec) => rec.autoIndicator
      },
      tuple._1.memberRole,
      tuple._1.memberType
    )

  )

  val UpdatingFuture = db.run(group2IdentTableQuery ++= idents)
  Await.result(UpdatingFuture, Duration.Inf)
}
