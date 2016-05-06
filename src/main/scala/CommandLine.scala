
import com.typesafe.config.ConfigFactory
import scripts.GooglePhotos
import google.services.Service
import google.services.Scopes.DRIVE
import google.services.Scopes.ADMIN_DIRECTORY

/**
  * Created by davenpcm on 5/3/16.
  */
object CommandLine extends App{


  val scope = ADMIN_DIRECTORY
  val config = ConfigFactory.load().getConfig("google")
  val serviceAccountEmail = config.getString("email")
  val credentialFilePath = config.getString("pkcs12FilePath")
  val applicationName = config.getString("applicationName")
  val adminImpersonatedEmail = config.getString("impersonatedEmail")

  val pluggableService = Service(serviceAccountEmail, credentialFilePath, applicationName, scope)(_)

//  val drive = pluggableService("davenpcm@eckerd.edu").Drive
//  val fileList = drive.files.list()
//  fileList.par.foreach(_.download("/home/davenpcm/Downloads/temp/", drive))

  val adminDirectory = pluggableService(adminImpersonatedEmail).Directory
  val files = adminDirectory.users.list().foreach(println)

//  val UserScope = DirectoryScopes.ADMIN_DIRECTORY_USER
//  val GroupScope = List(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val DriveScope = DriveScopes.DRIVE
//  val Scopes = ListScopes.foldRight("")((a,b) => a + "," + b).dropRight(1)



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



//    val adminService = partial(adminImpersonatedEmail)


//    val adminDirectory = pluggableService(adminImpersonatedEmail).Directory
//    adminDirectory.users.list().foreach(println)

//    val files = drive.files.list()
//
//    val randomFiles = files.filter(_.name.endsWith(".jpg")).take(10)
//    val Path = "/home/davenpcm/Downloads/temp/"
//    randomFiles.foreach(println)
//    randomFiles.par.foreach(_.download(Path, drive))




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

//  val directory = adminService.Directory
//  val photos = GooglePhotos.getAllGoogleImages("/home/davenpcm/Pictures/Student", directory)

//  val images = scripts.GooglePhotos.getAllGoogleImages("/home/davenpcm/Pictures/Student", directory)
//  images.foreach(println)

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_USER)
//  val gobumapResults = update(service)
//  gobumapResults.foreach(println)

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val update = scripts.GoogleUpdateGroupMaster.update(directory)
//  update foreach println

//  val service = getDirectory(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
//  val result = scripts.GoogleUpdateGroupToIdent.update(service)
//  result.foreach(println)
}
