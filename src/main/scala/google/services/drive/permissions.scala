package google.services.drive


import com.google.api.services.drive.model.Permission
import com.google.api.services.drive.model.PermissionList
import scala.collection.JavaConverters._
/**
  * Created by davenpcm on 5/4/16.
  */
class permissions(drive: Drive) {
  val service = drive.drive

  def list(fileId: String): List[Permission] = {
    val result = service.permissions().list(fileId)
      .execute()

    val typedList = List[PermissionList](result)
      .map(permissions => permissions.getPermissions.asScala.toList)
      .foldLeft(List[Permission]())((acc, listGroups) => listGroups ::: acc)

    typedList
  }

  def get(fileId: String, permissionId: String): Permission = {
    service.permissions().get(fileId, permissionId).execute()
  }

  def delete(fileId: String, permissionId: String): Unit = {
    service.permissions().delete(fileId, permissionId).execute()
  }

  def create(
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
