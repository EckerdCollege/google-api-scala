package google.services.drive.models

import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/4/16.
  */
case class Permission(emailAddress: String, role: String, permissionType: String, displayName: String)

object Permission {

  implicit def toGoogleApi(permission: Permission): com.google.api.services.drive.model.Permission = {
    val Permission = new com.google.api.services.drive.model.Permission()
      .setEmailAddress(permission.emailAddress)
      .setRole(permission.role)
      .setType(permission.permissionType)
      .setDisplayName(permission.displayName)
    Permission
  }

  implicit def fromGoogleApi(permission: com.google.api.services.drive.model.Permission): Permission = {
    Permission(permission.getEmailAddress, permission.getRole, permission.getType, permission.getDisplayName)
  }
}
