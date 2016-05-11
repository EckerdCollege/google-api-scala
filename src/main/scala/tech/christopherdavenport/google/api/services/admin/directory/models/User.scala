package tech.christopherdavenport.google.api.services.admin.directory.models

import language.implicitConversions
/**
  * Created by davenpcm on 5/6/16.
  */
case class User(name: Name,
                primaryEmail: Email,
                password: Option[String] = None,
                id: Option[String] = None,
                orgUnitPath: String = "/",
                agreedToTerms: Option[Boolean] = Some(false),
                changePasswordAtNextLogin: Boolean = false,
                includeInGlobalAddressList: Boolean = true,
                ipWhiteListed: Boolean = false,
                isAdmin: Boolean = false,
                isMailboxSetup: Boolean = false,
                suspended: Boolean = false
               )

object User {
  implicit def toMember(user: User): Member ={
    Member(
      Some(user.primaryEmail)
    )
  }
}