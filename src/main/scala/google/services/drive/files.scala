package google.services.drive

import models._

import scala.util.{Try, Success, Failure}


/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {
  private val service = drive.drive

  def list(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
    import scala.collection.JavaConverters._
    import com.google.api.services.drive.model.FileList
    val result = service.files().list()
      .setPageSize(500)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[FileList](result)
      .map(files => files.getFiles.asScala.toList)
      .foldLeft(List[File]())((acc, listGroups) => listGroups.map(File.fromGoogleApi) ::: acc)

    val myList = typedList ::: files

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
  }

  def listApplicationData(space: String = "appDataFolder", pageToken: String = "", files: List[File] = List[File]()): List[File] = {
    import scala.collection.JavaConverters._
    import com.google.api.services.drive.model.FileList
    val result = service.files().list()
      .setPageSize(500)
      .setSpaces(space)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[FileList](result)
      .map(files => files.getFiles.asScala.toList)
      .foldLeft(List[File]())((acc, listGroups) => listGroups.map(File.fromGoogleApi) ::: acc)

    val myList = typedList ::: files

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
  }

  def delete(fileId: String) = {
    service.files().delete(fileId).execute()
  }

  def create(file: File): File = file.content match {
    case None => service.files().create(file).execute()
    case Some(fileContent) => service.files().create(file, fileContent).execute()
  }

  def get(fileId: String): File =  {
    service.files().get(fileId).execute()
  }

  def update(file: File): File = {
    service.files().update(file.id.get, file).execute()
  }

  def getParents(file: File): Either[Throwable, File] = {
    val returnedFile: Try[File] = Try(service.files().get(file.id.get).setFields("parents").execute())
    returnedFile match {
      case Failure(e) => Left(e)
      case Success(gotFile) =>
        val parents = gotFile.parentIds
        Right(file.copy(parentIds = parents))
    }

  }

  def download(outputPath: String, file: File): Unit = {
    val id = file.id.get
    val filepath = outputPath + file.name
    val targetFile = new java.io.File(filepath)
    val outputStream = new java.io.FileOutputStream(targetFile)
    service.files().get(id).executeMediaAndDownloadTo(outputStream)
  }


}
