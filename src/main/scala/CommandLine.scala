import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import google.services.admin.directory._
import google.services.admin.directory.photos._
import google.services.service._
import scripts.GoogleUpdateGobumap.update
/**
  * Created by davenpcm on 5/3/16.
  */
object CommandLine extends App{
//  val ListScopes = List(
//    DirectoryScopes.ADMIN_DIRECTORY_USER,
//    DirectoryScopes.ADMIN_DIRECTORY_GROUP,
//    DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER,
//    CalendarScopes.CALENDAR,
//    DriveScopes.DRIVE,
//    GmailScopes.GMAIL_COMPOSE
//  )
//  val Scopes = ListScopes.foldRight("")((a,b) => a + "," + b).dropRight(1)


//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val these = groups.list(service)
//  these.foreach(println)

//  val directory = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_USER)
//  val images = scripts.GooglePhotos.getAllGoogleImages("/home/davenpcm/Pictures/Student", directory)
//  images.foreach(println)

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_USER)
//  val gobumapResults = update(service)
//  gobumapResults.foreach(println)

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val update = scripts.GoogleUpdateGroupMaster.update(service)
//  update foreach println

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val result = scripts.GoogleUpdateGroupToIdent.update(service)
//  result.foreach(println)
}
