package google.services.admin.directory.models
import language.implicitConversions
/**
  * Created by davenpcm on 5/6/16.
  */
case class Name(givenName: String, familyName: String)

object Name {
  implicit def toGoogleApi(name: Name): com.google.api.services.admin.directory.model.UserName = {
    new com.google.api.services.admin.directory.model.UserName()
      .setGivenName(name.givenName)
      .setFamilyName(name.familyName)
  }
  implicit def apply(userName: com.google.api.services.admin.directory.model.UserName): Name = {
    Name(
      userName.getGivenName,
      userName.getFamilyName
    )
  }
}
