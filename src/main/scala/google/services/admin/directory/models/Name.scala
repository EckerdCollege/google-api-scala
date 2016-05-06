package google.services.admin.directory.models
import language.implicitConversions
/**
  * Created by davenpcm on 5/6/16.
  */
case class Name(givenName: String, familyName: String, fullName: String)

object Name {
  implicit def toGoogleApi(name: Name): com.google.api.services.admin.directory.model.UserName = {
    new com.google.api.services.admin.directory.model.UserName()
      .setGivenName(name.givenName)
      .setFamilyName(name.familyName)
      .setFullName(name.fullName)
  }
  implicit def apply(userName: com.google.api.services.admin.directory.model.UserName): Name = {
    Name(
      userName.getGivenName,
      userName.getFamilyName,
      userName.getFullName
    )
  }
}
