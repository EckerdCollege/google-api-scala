import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.Permission
import com.google.api.services.gmail.GmailScopes
import com.typesafe.config.ConfigFactory
import google.services.drive.files._

/**
  * Created by davenpcm on 5/3/16.
  */
object CommandLine extends App{
  val ListScopes = List(
    DirectoryScopes.ADMIN_DIRECTORY_USER,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER,
    CalendarScopes.CALENDAR,
    DriveScopes.DRIVE,
    DriveScopes.DRIVE_APPDATA,
    GmailScopes.GMAIL_COMPOSE
  )

//  val UserScope = List(DirectoryScopes.ADMIN_DIRECTORY_USER)
//  val GroupScope = List(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val DriveScope = List(DriveScopes.DRIVE)
//  val Scopes = ListScopes.foldRight("")((a,b) => a + "," + b).dropRight(1)

  val config = ConfigFactory.load().getConfig("google")
  val serviceAccountEmail = config.getString("email")
  val credentialFilePath = config.getString("pkcs12FilePath")
  val applicationName = config.getString("applicationName")
  val adminImpersonatedEmail = config.getString("impersonatedEmail")

//  val service = google.services.service(
//    serviceAccountEmail,
//    adminImpersonatedEmail,
//    credentialFilePath,
//    applicationName,
//    ListScopes
//  )

//  val credential = service.credential
//
//  println(credential.getServiceAccountId)

    val partial = google.services.service(serviceAccountEmail, credentialFilePath, applicationName, ListScopes)(_)

    val me = partial("davenpcm@eckerd.edu")
    val admin = partial(adminImpersonatedEmail)

    println(me.credential)
    println(admin.credential)




//  val service = getCalendar(credential, applicationName)
//
//  val event = google.services.calendar.event.create("Best Phone Call Ever", "Awesome Phone Call", "2016-05-04T20:30:00-04:00", "2016-05-04T21:30:00-04:00", "davenpcm@eckerd.edu")
//  val finalevent = google.services.calendar.event.put(service, event)
//  val service = getDrive(credential, applicationName)

//  val these = google.services.drive.files.list(service)
//  val photoshop = these.filter(_.getMimeType == "image/x-photoshop")
//  photoshop.foreach(println)
//
//  val file = google.services.drive.files.generateMetaData("ChrisTestFolderShare", "Test Description", "application/vnd.google-apps.folder")
//  println(file)
//  val finishedFile = google.services.drive.files.upload(service, file)
//  println(finishedFile)
////
//  val mimeType = "image/png"
//  val picturePath = "/home/davenpcm/Downloads/vim_cheat_sheet_for_programmers_print.png"
//  val pictureName =  picturePath.substring(picturePath.lastIndexOf("/")+1)



//  val permission = new Permission()
//    .setEmailAddress("abneyfl@eckerd.edu")
//    .setRole("writer")
//    .setType("user")
//
//  println(permission)
//
//  val createpermission = google.services.drive.permissions.create(service, finishedFile.getId, permission, false)
//  println(createpermission)
//
//  val subFolder = google.services.drive.files.generateMetaData("ChrisTestSubFolder", "Random Description", "application/vnd.google-apps.folder", Some(List(finishedFile.getId)))
//  val finishedSubFolder = google.services.drive.files.upload(service, subFolder)
//  println(finishedSubFolder)

//  val imageMetaData = google.services.drive.files.generateMetaData(pictureName, "Cool Photo", mimeType, Some(List(finishedFile.getId)))
//  println(imageMetaData)
//  val imageContent = google.services.drive.files.generateFileContents(picturePath, mimeType)
//
//  val finishedImage = google.services.drive.files.upload(service, imageMetaData, imageContent)
//  println(finishedImage)

//    val path = "/home/davenpcm/Downloads/temp/"
////    val id = "1kvw_tvL7AkQGSoMj6M7Yh4jDAQBdQwTwc8jirAhXer8"
//    val listAllApp = google.services.drive.files.listApplicationData(service)
//    listAllApp.foreach(println)
//    val file = google.services.drive.files.get(service, id)
//    println(file)
//    google.services.drive.files.delete(service, id)
//    val download = google.services.drive.files.download(service, path, file)
//    println(download)

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
