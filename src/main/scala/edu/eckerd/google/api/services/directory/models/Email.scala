package edu.eckerd.google.api.services.directory.models

import scala.language.implicitConversions

case class Email(address: String, primary: Boolean)

object Email {

  def apply(emailAddress: String): Email = {
    Email(emailAddress, true)
  }

  implicit def address(email: Email): String = email.address

}