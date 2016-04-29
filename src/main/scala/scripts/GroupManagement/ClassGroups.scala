package scripts.GroupManagement

import persistence.entities.tables.{GROUPTOIDENT, GROUP_MASTER}
import utils.configuration.ConfigurationModuleImpl
import utils.persistence.PersistenceModuleImpl
import com.typesafe.slick.driver.oracle.OracleDriver.api._
import persistence.entities.constructs.{AutoEntity, AutoTable}
import persistence.entities.representations.{Group2Ident_R, GroupMaster_R}
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by davenpcm on 4/28/16.
  */
object ClassGroups {

  private object SavedState {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    implicit val db = modules.db

  }

  private def AlterBasedOnAutoTable[T <: AutoTable[A], A <: AutoEntity ](query: TableQuery[T])
                                                          (autoIndicator: String,  processIndicator: String)
                                                          (t: Query[T, A, Seq] => Unit) = {

    val selection = query.withFilter(record =>
    record.autoIndicator === autoIndicator && record.processIndicator === processIndicator)

    t(selection)
  }



  private def AlterOnGroup2Ident(autoIndicator: String,  processIndicator: String)(t: Query[GROUPTOIDENT, Group2Ident_R, Seq] => Unit) = {
    AlterBasedOnAutoTable[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT])(autoIndicator,  processIndicator)(t)
  }

  private def AlterOnGroup2IdentAutoDelete(t: Query[GROUPTOIDENT, Group2Ident_R, Seq] => Unit) = {
    AlterOnGroup2Ident("Y", "D")(t)
  }

  private def AlterOnGroup2IdentCreate(t: Query[GROUPTOIDENT, Group2Ident_R, Seq] => Unit) = {
    AlterOnGroup2Ident("Y", "C")(t)
  }

  private def DeleteGroup2IdentSelection(query: Query[GROUPTOIDENT, Group2Ident_R, Seq]): Unit = {
    SavedState.db.run(query.delete)

  }


  private def AddtoGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
    scripts.GoogleAdmin.AddUserToGroup(ident.groupId, ident.identID)
  }

  private def SelectionRemoveMembersFromGoogleGroup(query: Query[GROUPTOIDENT, Group2Ident_R, Seq]): Unit = {
    def RemoveFromGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
      scripts.GoogleAdmin.RemoveUserFromGroup(ident.groupId, ident.identID)
    }
    val result = Await.result(SavedState.db.run(query.result), Duration(5, "seconds"))
    result.foreach(RemoveFromGoogleByGroup2Ident(_))
  }




  private def DeleteMembersFromTable(): Unit = {
    AlterOnGroup2IdentAutoDelete(DeleteGroup2IdentSelection)
  }
  private def RemoveMembersFromGoogleGroup(): Unit = {
    AlterOnGroup2IdentAutoDelete(SelectionRemoveMembersFromGoogleGroup)
  }

  def RemoveMembersFromGoogle(): Unit = {
    RemoveMembersFromGoogleGroup()
    DeleteMembersFromTable()
  }



  private def RemoveFromGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    scripts.GoogleAdmin.DeleteGroup(group.id)
  }

  private def AddToGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    ???
  }


//  def AddMembersToGroups(): Unit = {
//    AlterBasedOnAutoTable[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT]
//    )(AddtoGoogleByGroup2Ident)("Y", "A")(a => Unit)
//  }
//
//  def deleteGroups(): Unit = {
//    AlterBasedOnAutoTable[GROUP_MASTER, GroupMaster_R](TableQuery[GROUP_MASTER]
//    )(RemoveFromGoogleByGroupMaster)("Y", "D")(DeleteSelectionFromTable)
//  }
  //  def deleteMembersFromGroups(): Unit = {
  //    AlterBasedOnAutoTable[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT]
  //    )(RemoveFromGoogleByGroup2Ident)("Y", "D")(DeleteSelectionFromTable)
  //  }
//private def DeleteSelectionFromTable[T <: AutoTable[A], A <: AutoEntity ](selection: Query[T, A, Seq] ): Unit = {
  //    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
  //    import modules.dbConfig.driver.api._
  //    Await.result( modules.db.run(selection.delete), Duration(10, "seconds"))
  //  }




}
