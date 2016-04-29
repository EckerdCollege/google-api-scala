package scripts.GroupManagement

import persistence.entities.tables.{GROUPTOIDENT, GROUP_MASTER}
import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.constructs.{AutoEntity, AutoTable}
import persistence.entities.representations.{Group2Ident_R, GroupMaster_R}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by davenpcm on 4/28/16.
  */
object ClassGroups {

  val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  implicit val db = modules.db



  private def AlterBasedOnAutoTable[T <: AutoTable[A], A <: AutoEntity ]
  (autoIndicator: String,  processIndicator: Option[String])
  (query: TableQuery[T])
  (t: Query[T, A, Seq] => Unit)
   = {

    val selection = query.withFilter(record =>
    record.autoIndicator === autoIndicator && record.processIndicator === processIndicator)
    println(selection)
    t(selection)
  }

  private def AlterOnAutoDelete[T <: AutoTable[A], A <: AutoEntity](query: TableQuery[T])
                               (t: Query[T, A, Seq] => Unit) = {
    AlterBasedOnAutoTable[T,A]("Y", Some("D"))(query)(t)
  }

  private def AlterOnAutoCreate[T <: AutoTable[A], A <: AutoEntity]
  (query: TableQuery[T])
  (t: Query[T, A, Seq] => Unit) = {
    AlterBasedOnAutoTable[T,A]("Y", Some("C"))(query)(t)
  }

  private def AlterOnGroup2IdentOnAutoDelete(t: Query[GROUPTOIDENT, Group2Ident_R, Seq] => Unit) = {
    AlterOnAutoDelete[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT])(t)
  }

  private def AlterOnGroup2IdentAutoCreate(t: Query[GROUPTOIDENT, Group2Ident_R, Seq] => Unit) = {
    AlterOnAutoCreate[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT])(t)
  }

  private def RemoveFromGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
    scripts.GoogleAdmin.RemoveUserFromGroup(ident.groupId, ident.identID)
  }

  private def AddToGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
    scripts.GoogleAdmin.AddUserToGroup(ident.groupId, ident.identID)
  }

  private def SelectionRemoveMembersFromGoogleGroup(query: Query[GROUPTOIDENT, Group2Ident_R, Seq]): Unit = {
    val result = Await.result(db.run(query.result), Duration(5, "seconds"))
    result.foreach(RemoveFromGoogleByGroup2Ident(_))
  }

  private def DeleteGroup2IdentSelection(query: Query[GROUPTOIDENT, Group2Ident_R, Seq]): Unit = {
    Await.result(db.run(query.delete), Duration(5, "seconds"))
  }

  private def SelectionCreateMemberGoogleGroup(query: Query[GROUPTOIDENT, Group2Ident_R, Seq]): Unit = {

    val result = Await.result(db.run(query.result), Duration(5, "seconds"))
    println(result)
    val newEntries = result.map(rec => rec.copy(processIndicator = None))
    println(newEntries)
    result.foreach(AddToGoogleByGroup2Ident(_))
    result.foreach(println(_))
    DeleteGroup2IdentSelection(query)
    Await.result(db.run(TableQuery[GROUPTOIDENT] ++= newEntries), Duration(5, "seconds"))
  }

  def CreateMembersGoogleGroup(): Unit = {
    AlterOnGroup2IdentAutoCreate(SelectionCreateMemberGoogleGroup)
  }


  def RemoveMembersFromGoogle(): Unit = {
    AlterOnGroup2IdentOnAutoDelete(SelectionRemoveMembersFromGoogleGroup)
    AlterOnGroup2IdentOnAutoDelete(DeleteGroup2IdentSelection)
  }




  private def RemoveFromGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    scripts.GoogleAdmin.DeleteGroup(group.id)
  }

  private def AddToGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    ???
  }



}
