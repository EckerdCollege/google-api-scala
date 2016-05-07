package google.services.drive.models


/**
  * Created by davenpcm on 5/4/16.
  */
case class Permission(emailAddress: String,
                      role: String,
                      permissionType: String,
                      displayName: String ,
                      id: Option[String] = None
                     )
