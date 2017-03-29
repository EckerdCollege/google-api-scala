package edu.eckerd.google.api.language

import com.google.api.services.admin.{directory => jDirectory}
import com.google.api.services.{calendar => jCalendar, drive => jDrive}
import edu.eckerd.google.api.services.{directory => sDirectory}
import edu.eckerd.google.api.services.{calendar => sCalendar, drive => sDrive}
import edu.eckerd.google.api.language.JavaConversions._

import scala.language.{implicitConversions, postfixOps}

object JavaConverters {

  class AsJava[C](op: => C){
    def asJava: C = op
  }

  class AsScala[C](op: => C){
    def asScala: C = op
  }

  class AsGoogle[C](op: => C){
    def asGoogle: C = op
  }

  implicit def scalaDirectoryAsJavaDirectoryConverter(b: edu.eckerd.google.api.services.directory.Directory): AsJava[jDirectory.Directory] = {
    new AsJava(scalaDirectoryAsJavaDirectoryConversion(b))
  }

  implicit def scalaGroupAsJavaGroupConverter(b: edu.eckerd.google.api.services.directory.models.Group): AsJava[jDirectory.model.Group] = {
    new AsJava(scalaGroupAsJavaGroupConversion(b))
  }

  implicit def javaGroupAsScalaGroupConverter(b: jDirectory.model.Group): AsScala[edu.eckerd.google.api.services.directory.models.Group] = {
    new AsScala(javaGroupAsScalaGroupConversion(b))
  }

  implicit def scalaGroupsAsJavaGroupsConverter(b: edu.eckerd.google.api.services.directory.models.Groups): AsJava[jDirectory.model.Groups] = {
    new AsJava(scalaGroupsAsJavaGroupsConversion(b))
  }

  implicit def javaGroupsAsScalaGroupsConverter(b: jDirectory.model.Groups): AsScala[edu.eckerd.google.api.services.directory.models.Groups] = {
    new AsScala(javaGroupsAsScalaGroupsConversion(b))
  }

  implicit def scalaMemberAsJavaMemberConverter(b: edu.eckerd.google.api.services.directory.models.Member): AsJava[jDirectory.model.Member] = {
    new AsJava(scalaMemberAsJavaMemberConversion(b))
  }

  implicit def javaMemberAsScalaMemberConverter(b: jDirectory.model.Member): AsScala[edu.eckerd.google.api.services.directory.models.Member] = {
    new AsScala(javaMemberAsScalaMemberConversion(b))
  }

  implicit def scalaListMemberAsJavaMembersConverter(b: edu.eckerd.google.api.services.directory.models.Members)
  : AsJava[jDirectory.model.Members] = {
    new AsJava(scalaMembersAsJavaMembersConversion(b))
  }

  implicit def javaMembersAsScalaListMemberConverter(b: jDirectory.model.Members):
  AsScala[edu.eckerd.google.api.services.directory.models.Members] = {
    new AsScala(javaMembersAsScalaMembersConversion(b))
  }

  implicit def scalaUserAsJavaUserConverter(b: edu.eckerd.google.api.services.directory.models.User): AsJava[jDirectory.model.User] = {
    new AsJava(scalaUserAsJavaUserConversion(b))
  }

  implicit def javaUserAsScalaUserConverter(b: jDirectory.model.User): AsScala[edu.eckerd.google.api.services.directory.models.User] = {
    new AsScala(javaUserAsScalaUserConversion(b))
  }

  implicit def scalaUsersAsJavaUsersConverter(b: edu.eckerd.google.api.services.directory.models.Users): AsJava[jDirectory.model.Users] = {
    new AsJava(scalaUsersAsJavaUsersConversion(b))
  }

  implicit def javaUsersAsScalaListUserConverter(b: jDirectory.model.Users): AsScala[edu.eckerd.google.api.services.directory.models.Users] = {
    new AsScala(javaUsersAsScalaUsersConversion(b))
  }

  implicit def scalaNameAsJavaUserNameConverter(b: edu.eckerd.google.api.services.directory.models.Name): AsJava[jDirectory.model.UserName] = {
    new AsJava(scalaNameAsJavaUserNameConversion(b))
  }

  implicit def javaUserNameAsScalaNameConverter(b: jDirectory.model.UserName): AsScala[edu.eckerd.google.api.services.directory.models.Name] = {
    new AsScala(javaUserNameAsScalaNameConversion(b))
  }

  implicit def scalaEmailAsJavaUserEmailConverter(b: edu.eckerd.google.api.services.directory.models.Email): AsJava[jDirectory.model.UserEmail] = {
    new AsJava(scalaEmailAsJavaUserEmailConversion(b))
  }

  implicit def javaUserEmailAsScalaEmailConverter(b: jDirectory.model.UserEmail): AsScala[edu.eckerd.google.api.services.directory.models.Email] = {
    new AsScala(javaUserEmailAsScalaEmailConversion(b))
  }

  implicit def scalaDriveAsJavaDriveConverter(b: edu.eckerd.google.api.services.drive.Drive): AsJava[jDrive.Drive] = {
    new AsJava(scalaDriveAsJavaDriveConversion(b))
  }

//  implicit def scalaFileAsJavaFileConverter(b: edu.eckerd.google.api.services.drive.models.File): AsJava[jDrive.model.File] = {
//    new AsJava(scalaFileAsJavaFileConversion(b))
//  }
//
//  implicit def javaFileAsScalaFileConverter(b: jDrive.model.File): AsScala[edu.eckerd.google.api.services.drive.models.File] = {
//    new AsScala(javaFileAsScalaFileConversion(b))
//  }
//
//  implicit def scalaListFileAsJavaFileListConverter(b: List[edu.eckerd.google.api.services.drive.models.File]): AsJava[jDrive.model.FileList] = {
//    new AsJava(scalaListFileAsJavaFileListConversion(b))
//  }
//
//  implicit def javaFileListAsScalaListFileConverter(b: jDrive.model.FileList): AsScala[List[edu.eckerd.google.api.services.drive.models.File]] = {
//    new AsScala(javaFileListAsScalaListFileConversion(b))
//  }

  implicit def scalaFileContentAsJavaFileContentConverter(b: edu.eckerd.google.api.services.drive.models.FileContent)
  : AsJava[com.google.api.client.http.FileContent] = {
    new AsJava(scalaFileContentAsJavaFileContentConversion(b))
  }

  implicit def scalaPermissionAsJavaPermissionConverter(b: edu.eckerd.google.api.services.drive.models.Permission):AsJava[jDrive.model.Permission] ={
    new AsJava(scalaPermissionAsJavaPermissionConversion(b))
  }

  implicit def javaPermissionAsScalaPermissionConverter(b: jDrive.model.Permission):AsScala[edu.eckerd.google.api.services.drive.models.Permission] ={
    new AsScala(javaPermissionAsScalaPermissionConversion(b))
  }

  implicit def scalaListPermissionAsJavaPermissionsListConverter(b: List[edu.eckerd.google.api.services.drive.models.Permission])
  : AsJava[jDrive.model.PermissionList] = {
    new AsJava(scalaListPermissionAsJavaPermissionsListConversion(b))
  }

  implicit def javaPermissionsListAsScalaListPermissionConverter(b: jDrive.model.PermissionList)
  : AsScala[List[edu.eckerd.google.api.services.drive.models.Permission]] = {
    new AsScala(javaPermissionsListAsScalaListPermissionsConversion(b))
  }

  implicit def scalaCalendarAsJavaCalendarConverter(b: edu.eckerd.google.api.services.calendar.Calendar): AsJava[jCalendar.Calendar] = {
    new AsJava(scalaCalendarAsJavaCalendarConversion(b))
  }

  implicit def scalaEventAsJavaEventConverter(b: edu.eckerd.google.api.services.calendar.models.Event): AsJava[jCalendar.model.Event] = {
    new AsJava(scalaEventAsJavaEventConversion(b))
  }

  implicit def javaEventAsScalaEventConverter(b: jCalendar.model.Event): AsScala[edu.eckerd.google.api.services.calendar.models.Event] = {
    new AsScala(javaEventAsScalaEventConversion(b))
  }

  implicit def javaZonedDateTimeAsGoogleDateTimeConverter(b: java.time.ZonedDateTime)
  : AsGoogle[com.google.api.client.util.DateTime] = {
    new AsGoogle(javaZonedDateTimeAsGoogleDateTimeConversion(b))
  }

  implicit def googleDateTimeAsJavaZonedDateTimeConverter(b: com.google.api.client.util.DateTime)
  : AsJava[java.time.ZonedDateTime] = {
    new AsJava(googleDateTimeAsJavaZoneDateTimeConversion(b))
  }

//  implicit def javaZoneDateTimeAsGoogleEventDateTimeConverter(b: java.time.ZonedDateTime)
//  : AsGoogle[jCalendar.model.EventDateTime] = {
//    new AsGoogle(javaZonedDateTimeAsGoogleEventDateTimeConversion(b))
//  }

//  implicit def googleEventDateTimeAsJavaZonedDateTimeConverter(b: jCalendar.model.EventDateTime)
//  : AsJava[java.time.ZonedDateTime] = {
//    new AsJava(googleEventDateTimeAsJavaZonedDateTimeConversion(b))
//  }


}
