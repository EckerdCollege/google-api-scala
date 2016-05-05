package google.services.drive

import models._
import scala.collection.JavaConverters._

/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {
  val service = drive.drive

  def list(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
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

  def upload(file: File): File = file.content match {
    case None => service.files().create(file).execute()
    case Some(fileContent) => service.files().create(file, fileContent).execute()
  }

  def get(fileId: String): File =  {
    service.files().get(fileId).execute()
  }

  def download(outputPath: String, file: File): Unit = {
    val id = file.getId
    val filepath = outputPath + file.getName
    val targetFile = new java.io.File(filepath)
    val outputStream = new java.io.FileOutputStream(targetFile)

    service.files().get(id).executeMediaAndDownloadTo(outputStream)
  }


}
