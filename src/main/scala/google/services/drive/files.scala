package google.services.drive

import google.language.JavaConverters
import models._

import language.implicitConversions
import language.postfixOps
import scala.annotation.tailrec
import JavaConverters._


/**
  * Created by davenpcm on 5/4/16.
  */
class files(drive: Drive) {

  private val service = drive.asJava

  def list() : List[File] = {
    @tailrec
    def list(pageToken: String = "", files: List[File] = List[File]()): List[File] = {

      val result = service.files().list()
        .setPageSize(500)
        .setPageToken(pageToken)
        .execute()

      val typedList = result.asScala

      val myList = typedList ::: files

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getFiles != null) list(nextPageToken, myList) else myList
    }
    list()
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

  def create(file: File): File = file.content match {
    case None => service.files().create(file.asJava).execute().asScala
    case Some(fileContent) => service.files().create(file.asJava, fileContent.asJava).execute().asScala
  }

  def get(fileId: String): File =  {
    service.files().get(fileId).execute().asScala
  }

  def update(file: File): File = {
    service.files().update(file.id.get, file.asJava).execute().asScala
  }

  def getParents(file: File): File = {
    val returnedFile: File = service.files().get(file.id.get).setFields("parents").execute().asScala
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
