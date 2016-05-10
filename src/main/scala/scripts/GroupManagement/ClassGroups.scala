package scripts.GroupManagement

import google.services.admin.directory.Directory
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


  private def RemoveFromGoogleByGroup2Ident(ident: Group2Ident_R, service: Directory): Unit = {
    service.members.remove(ident.groupId, ident.identID)
  }

  private def AddToGoogleByGroup2Ident(ident: Group2Ident_R, service: Directory): Unit = {
    service.members.add(ident.groupId, ident.identID)
  }



}
