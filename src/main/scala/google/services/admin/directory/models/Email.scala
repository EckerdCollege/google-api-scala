package google.services.admin.directory.models

import scala.language.implicitConversions

/**
  * Created by davenpcm on 5/6/16.
  */
case class Email(address: String, primary: Boolean)

object Email {
  implicit def toGoogleApi(email: Email): com.google.api.services.admin.directory.model.UserEmail = {
    new com.google.api.services.admin.directory.model.UserEmail()
      .setAddress(email.address)
      .setPrimary(email.primary)
  }

  implicit def apply(userEmail: com.google.api.services.admin.directory.model.UserEmail): Email = {
    Email(
      userEmail.getAddress,
      userEmail.getPrimary
    )
  }

}