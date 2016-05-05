//package google.services.drive.models
//
///**
//  * Created by davenpcm on 5/4/16.
//  */
//case class Permission( service: Drive,
//                       fileId: String,
//                       permission: Permission,
//                       sendNotificationEmail: Boolean,
//                       emailMessage: String = "",
//                       transferOwnership: Boolean = false) {
//
//  implicit def toGooglePermission: com.google.api.services.drive.model.Permission = {
//    val initService = service.permissions().create(fileId, permission)
//      .setTransferOwnership(transferOwnership)
//      .setSendNotificationEmail(sendNotificationEmail)
//
//    sendNotificationEmail match {
//      case true => initService.setEmailMessage(emailMessage).execute()
//      case false => initService.execute()
//
//    }
//  }
//}
