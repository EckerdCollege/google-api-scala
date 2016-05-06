package google.services
import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes

/**
  * Created by davenpcm on 5/6/16.
  */
object Scopes {
  val ADMIN_DIRECTORY_USER = DirectoryScopes.ADMIN_DIRECTORY_USER
  val ADMIN_DIRECTORY_USER_READONLY = DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY
  val ADMIN_DIRECTORY_GROUP = DirectoryScopes.ADMIN_DIRECTORY_GROUP
  val ADMIN_DIRECTORY_GROUP_READONLY = DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY
  val ADMIN_DIRECTORY_GROUP_MEMBER = DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER
  val ADMIN_DIRECTORY_GROUP_MEMBER_READONLY = DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER_READONLY
  val DRIVE_CONTROL = DriveScopes.DRIVE
  val DRIVE_CONTROL_APPDATA = DriveScopes.DRIVE_APPDATA
  val GMAIL_COMPOSE = GmailScopes.GMAIL_COMPOSE
  val GMAIL_SEND = GmailScopes.GMAIL_SEND
  val GMAIL_READONLY = GmailScopes.GMAIL_READONLY

  val ADMIN_DIRECTORY = List(ADMIN_DIRECTORY_USER, ADMIN_DIRECTORY_GROUP, ADMIN_DIRECTORY_GROUP_MEMBER)
  val ADMIN_DIRECTORY_READONLY = List(ADMIN_DIRECTORY_USER_READONLY, ADMIN_DIRECTORY_GROUP_READONLY, ADMIN_DIRECTORY_GROUP_MEMBER_READONLY)
  val DRIVE = List(DRIVE_CONTROL, DRIVE_CONTROL_APPDATA)

  val ALL = List(
    ADMIN_DIRECTORY_USER,
    ADMIN_DIRECTORY_GROUP,
    ADMIN_DIRECTORY_GROUP_MEMBER,
    DRIVE_CONTROL,
    DRIVE_CONTROL_APPDATA,
    GMAIL_COMPOSE,
    GMAIL_SEND
  )


}
