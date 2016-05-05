package google.services.drive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.model.{File, FileList}

import scala.collection.JavaConverters._
/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {
  val service = drive.drive

  def list(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
    val result = service.files().list()
      .setPageSize(500)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[FileList](result)
      .map(files => files.getFiles.asScala.toList)
      .foldLeft(List[File]())((acc, listGroups) => listGroups ::: acc)

    val myList = typedList ::: files

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
  }

  def listApplicationData( pageToken: String = "", files: List[File] = List[File]()): List[File] = {
    val result = service.files().list()
      .setPageSize(500)
      .setSpaces("appDataFolder")
      .setPageToken(pageToken)
      .execute()

    val typedList = List[FileList](result)
      .map(files => files.getFiles.asScala.toList)
      .foldLeft(List[File]())((acc, listGroups) => listGroups ::: acc)

    val myList = typedList ::: files

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
  }

  def generateMetaData(name: String,
               description: String,
               mimeType: String,
               parentIds: Option[List[String]] = None
              ): File = {
    val metadata = new File()
      .setName(name)
      .setDescription(description)
      .setMimeType(mimeType)

    if (parentIds.isDefined){
      metadata.setParents(parentIds.get.asJava)
    }

    metadata
  }
  def generateFileContents(fileName: String, mimeType: String): Option[FileContent] = {
    val fileContent = new java.io.File(fileName)
    val mediaContent = new FileContent(mimeType, fileContent)
    Some(mediaContent)
  }

  def delete(fileId: String) = {
    service.files().delete(fileId).execute()
  }


  def upload(metaData: File, content: Option[FileContent] = None): File = content match {
    case None => service.files().create(metaData).execute()
    case Some(fileContent) => service.files().create(metaData, fileContent).execute()

  }

  def get(fileId: String): File =  {
    service.files().get(fileId).execute()
  }

  def download(outputPath: String, file: File): Unit = {
    val id = file.getId
    val filepath = outputPath + file.getName
    println(filepath)
    val targetFile = new java.io.File(filepath)
    val outputStream = new java.io.FileOutputStream(targetFile)

    service.files().get(id).executeMediaAndDownloadTo(outputStream)
  }


}
