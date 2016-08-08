package edu.eckerd.google.api.services.drive


import edu.eckerd.google.api.language.JavaConverters._
import models._
import edu.eckerd.google.api.services.drive.models.File._
import edu.eckerd.google.api.services.drive.models.FileList._
import language.implicitConversions
import language.postfixOps
import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {

  private val service = drive.asJava

  def list : List[File] = {
    @tailrec
    def list( pageToken: String = "", files: List[File] = List[File]()): List[File] = listPage(pageToken) match {
        case FileListPage(thisPageList, nextPageToken) =>
          list(nextPageToken, thisPageList ::: files)
        case CompleteFileList(thisPageList) =>
          thisPageList ::: files
    }
    list()
  }

  def listPage(pageToken: String): FileList = {
   service.files().list()
      .setPageSize(500)
      .setPageToken(pageToken)
      .execute()
      .asScala
  }

  def listSpace(space: String = "appDataFolder"): List[File] = {
    @tailrec
    def listSpace(pageToken: String = "", files: List[File] = List[File]()): List[File] = {
      val result = service.files().list()
        .setPageSize(500)
        .setSpaces(space)
        .setPageToken(pageToken)
        .execute()

      val typedList = result.asScala

      val myList = typedList ::: files

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getFiles != null) listSpace(nextPageToken, myList) else myList
    }
    listSpace()
  }

  def delete(fileId: String) = {
    service.files().delete(fileId).execute()
  }

  def create(file: File, fileContent: FileContent): File = {
//    case None => service.files().create(file.asJava).execute().asScala
    service.files().create(file.asJava, fileContent.asJava).execute().asScala
  }

  def get(fileId: String): File =  {
    service.files().get(fileId)
        .setFields("id, name, mimeType, size, description, createdTime, modifiedTime, parents, trashed")
      .execute()
      .asScala
  }

  def get(file: File): File = {
    get(file.id)
  }

  def update(file: File): File = {
    service.files().update(file.id, file.asJava).execute().asScala
  }

  def download(outputPath: String, file: File): Unit = {
    val id = file.id
    val filepath = outputPath + file.name
    val targetFile = new java.io.File(filepath)
    val outputStream = new java.io.FileOutputStream(targetFile)
    service.files().get(id).executeMediaAndDownloadTo(outputStream)
  }


}
