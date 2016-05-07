package google.services.drive

import models._
import language.implicitConversions
import language.postfixOps
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}


/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {

  private val service: com.google.api.services.drive.Drive = drive

  private implicit def fileContentToGoogleApi(fileContent: FileContent): com.google.api.client.http.FileContent = {
    new com.google.api.client.http.FileContent(fileContent.mimeType, fileContent.content)
  }

  private implicit def fileToGoogleApi(file: File): com.google.api.services.drive.model.File = {
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

  private implicit def fileFromGoogleApi(file: com.google.api.services.drive.model.File): File = {
    import scala.collection.JavaConverters._
    File(
      file.getName,
      file.getMimeType,
      Option(file.getId),
      Option(file.getFileExtension),
      Option(file.getDescription),
      Option(file.getParents).map(_.asScala.toList)
    )

  }

  def list : List[File] = {
    @tailrec
    def list(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
      import scala.collection.JavaConverters._
      import com.google.api.services.drive.model.FileList
      val result = service.files().list()
        .setPageSize(500)
        .setPageToken(pageToken)
        .execute()

      val typedList = List[FileList](result)
        .map(files => files.getFiles.asScala.toList)
        .foldLeft(List[File]())((acc, listGroups) => listGroups.map(fileFromGoogleApi) ::: acc)

      val myList = typedList ::: files

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
    }
    list()
  }

  def listSpace(space: String = "appDataFolder"): List[File] = {
    @tailrec
    def listSpace(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
      import scala.collection.JavaConverters._
      import com.google.api.services.drive.model.FileList
      val result = service.files().list()
        .setPageSize(500)
        .setSpaces(space)
        .setPageToken(pageToken)
        .execute()

      val typedList = List[FileList](result)
        .map(files => files.getFiles.asScala.toList)
        .foldLeft(List[File]())((acc, listGroups) => listGroups.map(fileFromGoogleApi) ::: acc)

      val myList = typedList ::: files

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getFiles != null) listSpace(nextPageToken, myList) else myList
    }
    listSpace()
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

  def getParents(file: File): File = {
    val returnedFile: File = service.files().get(file.id.get).setFields("parents").execute()
    val parents = returnedFile.parentIds
    file.copy(parentIds = parents)

  }

  def download(outputPath: String, file: File): Unit = {
    val id = file.id.get
    val filepath = outputPath + file.name
    val targetFile = new java.io.File(filepath)
    val outputStream = new java.io.FileOutputStream(targetFile)
    service.files().get(id).executeMediaAndDownloadTo(outputStream)
  }


}
