package google.services.drive

import language.implicitConversions
import language.postfixOps
import models._

/**
  * Created by davenpcm on 5/4/16.
  */
class permissions(drive: Drive) {
  private val service = drive.drive

  private implicit def toGoogleApi(permission: Permission): com.google.api.services.drive.model.Permission = {
    val Permission = new com.google.api.services.drive.model.Permission()
      .setRole(permission.role)
      .setType(permission.permissionType)

    if (permission.emailAddress isDefined){ Permission.setEmailAddress(permission.emailAddress.get) }
    if (permission.displayName isDefined){ Permission.setDisplayName(permission.displayName.get) }
    if (permission.id isDefined) { Permission.setId(permission.id.get)}
    Permission
  }

  private implicit def fromGoogleApi(permission: com.google.api.services.drive.model.Permission): Permission = {
    Permission(
      permission.getRole,
      permission.getType,
      Option(permission.getEmailAddress),
      Option(permission.getDisplayName),
      Option(permission.getId)
    )
  }

  def list(fileId: String): List[Permission] = {
    import com.google.api.services.drive.model.PermissionList
    import scala.collection.JavaConverters._

    val result = service.permissions().list(fileId)
      .execute()

    val typedList = List[PermissionList](result)
      .map(permissions => permissions.getPermissions.asScala.toList)
      .foldLeft(List[Permission]())((acc, listGroups) => listGroups.map(fromGoogleApi) ::: acc)

    typedList
  }

  def get(fileId: String, permissionId: String): Permission = {
    service.permissions().get(fileId, permissionId).setFields("id, type, emailAddress, role, displayName").execute()
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
    val initService = service.permissions().create(file.id.get, permission)
      .setTransferOwnership(transferOwnership)
      .setSendNotificationEmail(sendNotificationEmail)

    sendNotificationEmail match {
      case true => initService.setEmailMessage(emailMessage).execute()
      case false => initService.execute()

    }
  }

  def createById(
             fileId: String,
             permission: Permission,
             sendNotificationEmail: Boolean,
             emailMessage: String = "",
             transferOwnership: Boolean = false): Permission = {
    val initService = service.permissions().create(fileId, permission)
      .setTransferOwnership(transferOwnership)
      .setSendNotificationEmail(sendNotificationEmail)

    sendNotificationEmail match {
      case true => initService.setEmailMessage(emailMessage).execute()
      case false => initService.execute()

    }
  }



}
