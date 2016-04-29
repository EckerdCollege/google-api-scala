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

  private def AlterBasedOnAutoTable[T <: AutoTable[A], A <: AutoEntity ](query: TableQuery[T])
                                                          (f: A => Unit)
                                                          (autoIndicator: String,  processIndicator: String)
                                                          (t: Query[T, A, Seq] => Unit) = {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modules.dbConfig.driver.api._
    val selection = query.withFilter(record =>
    record.autoIndicator === autoIndicator && record.processIndicator === processIndicator)
    val recordsToProcess = Await.result( modules.db.run(selection.result), Duration(10, "seconds"))
    recordsToProcess.par.foreach(f(_))
    t(selection)
  }

  private def DeleteSelectionFromTable[T <: AutoTable[A], A <: AutoEntity ](selection: Query[T, A, Seq] ): Unit = {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modules.dbConfig.driver.api._
    Await.result( modules.db.run(selection.delete), Duration(10, "seconds"))
  }

  private def SetProcessIndToNull[T <: AutoTable[A], A <: AutoEntity ](selection: Query[T, A, Seq]): Unit = {
    val modules = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modules.dbConfig.driver.api._
    val results = Await.result( modules.db.run(selection.result), Duration(10, "seconds"))

    selection.update(_.g)

    modified.par.foreach(rec =>
    Await.result( modules.db.run(selection.update(rec)), Duration(10, "seconds")))
    ???
  }

  private def RemoveFromGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    scripts.GoogleAdmin.DeleteGroup(group.id)
  }

  private def AddToGoogleByGroupMaster(group: GroupMaster_R): Unit = {
    ???
  }

  private def AddtoGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
    scripts.GoogleAdmin.AddUserToGroup(ident.groupId, ident.identID)
  }

  private def RemoveFromGoogleByGroup2Ident(ident: Group2Ident_R): Unit = {
    scripts.GoogleAdmin.RemoveUserFromGroup(ident.groupId, ident.identID)
  }

  def deleteMembersFromGroups(): Unit = {
    AlterBasedOnAutoTable[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT]
    )(RemoveFromGoogleByGroup2Ident)("Y", "D")(DeleteSelectionFromTable)
  }

  def AddMembersToGroups(): Unit = {
    AlterBasedOnAutoTable[GROUPTOIDENT, Group2Ident_R](TableQuery[GROUPTOIDENT]
    )(AddtoGoogleByGroup2Ident)("Y", "A")(a => Unit)
  }

  def deleteGroups(): Unit = {
    AlterBasedOnAutoTable[GROUP_MASTER, GroupMaster_R](TableQuery[GROUP_MASTER]
    )(RemoveFromGoogleByGroupMaster)("Y", "D")(DeleteSelectionFromTable)
  }




}
