package tech.christopherdavenport.google.api.services.drive


import tech.christopherdavenport.google.api.language.JavaConverters._

import language.implicitConversions
import language.postfixOps
import models._

/**
  * Created by davenpcm on 5/4/16.
  */
class permissions(drive: Drive) {
  private val service = drive.asJava


  def list(fileId: String): List[Permission] = {

    val result = service.permissions().list(fileId)
      .execute().asScala

    result
  }

  def get(fileId: String, permissionId: String): Permission = {
    service.permissions()
      .get(fileId, permissionId)
      .setFields("id, type, emailAddress, role, displayName")
      .execute()
      .asScala
  }

  def deleteById(fileId: String, permissionId: String): Unit = {
    service.permissions().delete(fileId, permissionId).execute()
  }

  def create(
            file: File,
            permission: Permission,
            sendNotificationEmail: Boolean,
            emailMessage: String = "",
            transferOwnership: Boolean = false): Permission = {
    val initService = service.permissions().create(file.id.get, permission.asJava)
      .setTransferOwnership(transferOwnership)
      .setSendNotificationEmail(sendNotificationEmail)

    sendNotificationEmail match {
      case true => initService.setEmailMessage(emailMessage).execute().asScala
      case false => initService.execute().asScala

    }
  }

  def createById(
             fileId: String,
             permission: Permission,
             sendNotificationEmail: Boolean,
             emailMessage: String = "",
             transferOwnership: Boolean = false): Permission = {
    val initService = service.permissions().create(fileId, permission.asJava)
      .setTransferOwnership(transferOwnership)
      .setSendNotificationEmail(sendNotificationEmail)

    sendNotificationEmail match {
      case true => initService.setEmailMessage(emailMessage).execute().asScala
      case false => initService.execute().asScala

    }
  }



}
