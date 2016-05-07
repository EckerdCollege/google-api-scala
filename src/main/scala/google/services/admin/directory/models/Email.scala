package google.services.admin.directory.models

import scala.language.implicitConversions

/**
  * Created by davenpcm on 5/6/16.
  */
case class Email(address: String, primary: Boolean)

object Email {

  def apply(emailAddress: String): Email = {
    Email(emailAddress, true)
  }

  implicit def address(email: Email):String = email.address

}