package google.services.admin.directory.models

import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/6/16.
  */
case class User(name: Name,
                emails: List[Email],
                id: Option[String] = None,
                orgUnitPath: String = "/",
                agreedToTerms: Boolean = false,
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
    import scala.collection.JavaConverters._

    val primaryEmail = user.emails.find(_.primary == true).get.address

    val newUser = new com.google.api.services.admin.directory.model.User
    newUser
      .setAgreedToTerms(user.agreedToTerms)
      .setChangePasswordAtNextLogin(user.changePasswordAtNextLogin)
      .setIncludeInGlobalAddressList(user.includeInGlobalAddressList)
      .setIpWhitelisted(user.ipWhiteListed)
      .setIsAdmin(user.isAdmin)
      .setIsMailboxSetup(user.isMailboxSetup)
      .setSuspended(user.suspended)
      .setOrgUnitPath(user.orgUnitPath)
      .setEmails(user.emails.asJava)
      .setPrimaryEmail(primaryEmail)
  }

  implicit def fromGoogleApi(user: com.google.api.services.admin.directory.model.User): User = {
    User(
      user.getName,
      List(Email(user.getPrimaryEmail, true)),
      Option(user.getId),
      user.getOrgUnitPath,
      user.getAgreedToTerms,
      user.getChangePasswordAtNextLogin,
      user.getIncludeInGlobalAddressList,
      user.getIpWhitelisted,
      user.getIsAdmin,
      user.getIsMailboxSetup,
      user.getSuspended
    )
  }

}
