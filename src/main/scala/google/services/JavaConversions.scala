package google.services

import google.services.admin.{directory => sDirectory}
import com.google.api.services.admin.{directory => jDirectory}
import google.services.{drive => sDrive}
import com.google.api.services.{drive => jDrive}
import scala.language.implicitConversions
import scala.language.postfixOps

/**
  * Created by davenpcm on 5/7/16.
  */
object JavaConversions {

  implicit def scalaDirectoryAsJavaDirectoryConversion(b: sDirectory.Directory): jDirectory.Directory = {
    new com.google.api.services.admin.directory.Directory.Builder(
      b.service.httpTransport,
      b.service.jsonFactory,
      b.service.credential)
      .setApplicationName(b.service.applicationName)
      .setHttpRequestInitializer(b.service.credential)
      .build()
  }

  implicit def scalaGroupAsJavaGroupConversion(b: sDirectory.models.Group): jDirectory.model.Group = {
    val newGroup = new com.google.api.services.admin.directory.model.Group()
      .setName(b.name)
      .setEmail(b.email)

    if (b.id isDefined) { newGroup.setId(b.id.get) }
    if (b.description isDefined) { newGroup.setDescription(b.description.get) }
    if (b.directMemberCount isDefined){ newGroup.setDirectMembersCount(b.directMemberCount.get)}
    if (b.adminCreated isDefined){ newGroup.setAdminCreated(b.adminCreated.get)}

    newGroup
  }

  implicit def javaGroupAsScalaGroupConversion(b: jDirectory.model.Group): sDirectory.models.Group = {
    sDirectory.models.Group(
      b.getName,
      b.getEmail,
      Option(b.getId),
      Option(b.getDescription),
      Option(b.getDirectMembersCount),
      Option(b.getAdminCreated)
    )
  }

  implicit def scalaMemberAsJavaMemberConversion(b: sDirectory.models.Member): jDirectory.model.Member = {
    val newMember = new com.google.api.services.admin.directory.model.Member()
      .setRole(b.role)
      .setType(b.memberType)

    if (b.email isDefined){ newMember.setEmail(b.email.get)}
    if (b.id isDefined){ newMember.setId(b.id.get)}

    newMember
  }

  implicit def javaMemberAsScalaMemberConversion(member: jDirectory.model.Member): sDirectory.models.Member = {
    sDirectory.models.Member(
      Option(member.getEmail),
      Option(member.getId),
      member.getRole,
      member.getType
    )
  }

  implicit def scalaUserAsJavaUserConversion(user: sDirectory.models.User): jDirectory.model.User = {
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

  implicit def javaUserAsScalaUserConversion(user: jDirectory.model.User): sDirectory.models.User = {
    sDirectory.models.User(
      user.getName,
      sDirectory.models.Email(user.getPrimaryEmail),
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

  implicit def scalaNameAsJavaUserNameConversion(name: sDirectory.models.Name): jDirectory.model.UserName = {
    new com.google.api.services.admin.directory.model.UserName()
      .setGivenName(name.givenName)
      .setFamilyName(name.familyName)
  }

  implicit def javaUserNameAsScalaNameConversion(userName: jDirectory.model.UserName): sDirectory.models.Name = {
    sDirectory.models.Name(
      userName.getGivenName,
      userName.getFamilyName
    )
  }

  implicit def scalaEmailAsJavaUserEmailConversion(email: sDirectory.models.Email): jDirectory.model.UserEmail = {
    new com.google.api.services.admin.directory.model.UserEmail()
      .setAddress(email.address)
      .setPrimary(email.primary)
  }

  implicit def javaUserEmailAsScalaEmailConversion(userEmail: jDirectory.model.UserEmail): sDirectory.models.Email = {
    sDirectory.models.Email(
      userEmail.getAddress,
      userEmail.getPrimary
    )
  }


}
