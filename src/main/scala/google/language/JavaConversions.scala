package google.language

import com.google.api.services.admin.{directory => jDirectory}
import com.google.api.services.{calendar => jCalendar, drive => jDrive}
import google.services.admin.{directory => sDirectory}
import google.services.{calendar => sCalendar, drive => sDrive}

import scala.language.{implicitConversions, postfixOps}

/**
  * Created by davenpcm on 5/7/16.
  */
object JavaConversions {

  implicit def scalaDirectoryAsJavaDirectoryConversion(b: sDirectory.Directory): jDirectory.Directory = {
    new com.google.api.services.admin.directory.Directory.Builder(
      b.httpTransport,
      b.jsonFactory,
      b.credential)
      .setApplicationName(b.applicationName)
      .setHttpRequestInitializer(b.credential)
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

  implicit def scalaListUserAsJavaUsersConversion(b: List[sDirectory.models.User]): jDirectory.model.Users = {
    import scala.collection.JavaConverters._
    val users = b.map(scalaUserAsJavaUserConversion(_)).asJava
    new jDirectory.model.Users()
      .setUsers(users)
  }

  implicit def javaUsersAsScalaListUserConversion(b: jDirectory.model.Users): List[sDirectory.models.User] = {
    import scala.collection.JavaConverters._
    b.getUsers.asScala.toList.map(javaUserAsScalaUserConversion)
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

  implicit def scalaDriveAsJavaDriveConversion(b: sDrive.Drive): jDrive.Drive = {
    new com.google.api.services.drive.Drive.Builder(
      b.httpTransport,
      b.jsonFactory,
      b.credential)
      .setApplicationName(b.applicationName)
      .setHttpRequestInitializer(b.credential)
      .build()
  }

  implicit def scalaFileContentAsJavaFileContentConversion(fileContent: sDrive.models.FileContent)
  : com.google.api.client.http.FileContent = {
    new com.google.api.client.http.FileContent(fileContent.mimeType, fileContent.content)
  }

  implicit def scalaFileAsJavaFileConversion(file: sDrive.models.File): jDrive.model.File = {
    import scala.collection.JavaConverters._
    val metadata = new com.google.api.services.drive.model.File()
      .setName(file.name)
      .setMimeType(file.mimeType)
    if (file.id isDefined) { metadata.setId(file.id.get)}
    if (file.extension isDefined){ metadata.setFileExtension(file.extension.get)}
    if (file.description isDefined){ metadata.setDescription(file.description.get)}
    if (file.parentIds isDefined){ metadata.setParents(file.parentIds.get.asJava)}
    metadata
  }

  implicit def javaFileAsScalaFileConversion(file: jDrive.model.File): sDrive.models.File = {
    import scala.collection.JavaConverters._
    sDrive.models.File(
      file.getName,
      file.getMimeType,
      Option(file.getId),
      Option(file.getFileExtension),
      Option(file.getDescription),
      Option(file.getParents).map(_.asScala.toList)
    )
  }

  implicit def scalaListFileAsJavaFileListConversion(b: List[sDrive.models.File]): jDrive.model.FileList = {
    import collection.JavaConverters._
    val files = b.map(scalaFileAsJavaFileConversion).asJava
    new jDrive.model.FileList()
      .setFiles(files)
  }

  implicit def javaFileListAsScalaListFileConversion(b: jDrive.model.FileList): List[sDrive.models.File] = {
    import collection.JavaConverters._
    b.getFiles.asScala.toList.map(javaFileAsScalaFileConversion)
  }

  implicit def scalaPermissionAsJavaPermissionConversion(permission: sDrive.models.Permission)
  : jDrive.model.Permission = {

    val Permission = new com.google.api.services.drive.model.Permission()
      .setRole(permission.role)
      .setType(permission.permissionType)

    if (permission.emailAddress isDefined){ Permission.setEmailAddress(permission.emailAddress.get) }
    if (permission.displayName isDefined){ Permission.setDisplayName(permission.displayName.get) }
    if (permission.id isDefined) { Permission.setId(permission.id.get)}
    Permission
  }

  implicit def javaPermissionAsScalaPermissionConversion(permission: jDrive.model.Permission)
  : sDrive.models.Permission = {
    sDrive.models.Permission(
      permission.getRole,
      permission.getType,
      Option(permission.getEmailAddress),
      Option(permission.getDisplayName),
      Option(permission.getId)
    )
  }

  implicit def scalaListPermissionAsJavaPermissionsListConversion(b: List[sDrive.models.Permission])
  : jDrive.model.PermissionList = {
    import scala.collection.JavaConverters._
    val javaPermissions = b.map(scalaPermissionAsJavaPermissionConversion)
    new jDrive.model.PermissionList()
      .setPermissions(javaPermissions.asJava)
  }

  implicit def javaPermissionsListAsScalaListPermissionsConversion(b: jDrive.model.PermissionList)
  : List[sDrive.models.Permission] = {
    import scala.collection.JavaConverters._
    b.getPermissions.asScala.toList.map(javaPermissionAsScalaPermissionConversion)
  }

  implicit def scalaCalendarAsJavaCalendarConversion(service: sCalendar.Calendar): jCalendar.Calendar = {
    new com.google.api.services.calendar.Calendar.Builder(service.httpTransport, service.jsonFactory, service.credential)
      .setApplicationName(service.applicationName)
      .setHttpRequestInitializer(service.credential)
      .build()
  }


}
