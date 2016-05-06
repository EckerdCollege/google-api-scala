package google.services.admin.directory.models

import scala.language.implicitConversions
import scala.language.postfixOps
import scala.util.{Success, Failure, Try}
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
               ) {

}

object User {

  implicit def toGoogleApi(user: User): com.google.api.services.admin.directory.model.User = {
    val newUser = new com.google.api.services.admin.directory.model.User
    newUser
      .setChangePasswordAtNextLogin(user.changePasswordAtNextLogin)
      .setIncludeInGlobalAddressList(user.includeInGlobalAddressList)
      .setIpWhitelisted(user.ipWhiteListed)
      .setIsAdmin(user.isAdmin)
      .setIsMailboxSetup(user.isMailboxSetup)
      .setSuspended(user.suspended)
      .setOrgUnitPath(user.orgUnitPath)
      .setPrimaryEmail(user.primaryEmail)
      .setName(user.name)

    if (user.agreedToTerms isDefined) { newUser.setAgreedToTerms( user.agreedToTerms.get)}
    if (user.password isDefined) { newUser.setPassword( user.password.get)}


    newUser
  }

  implicit def fromGoogleApi(user: com.google.api.services.admin.directory.model.User): User = {
    User(
      user.getName,
      Email(user.getPrimaryEmail),
      Option(user.getPassword),
      Option(user.getId),
      user.getOrgUnitPath,
      Option(user.getAgreedToTerms) match {
        case Some(value ) => Some(value)
        case None => None
      },
      user.getChangePasswordAtNextLogin,
      user.getIncludeInGlobalAddressList,
      user.getIpWhitelisted,
      user.getIsAdmin,
      user.getIsMailboxSetup,
      user.getSuspended
    )
  }

}
