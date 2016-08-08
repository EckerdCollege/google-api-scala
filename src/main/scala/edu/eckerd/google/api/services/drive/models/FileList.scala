package edu.eckerd.google.api.services.drive.models

import com.google.api.services.drive.model.{FileList => jFileList}
import edu.eckerd.google.api.services.drive.models.File._
import scala.language.implicitConversions
/**
  * Created by davenpcm on 8/6/16.
  */

trait FileList{
  val list: List[File]
}

case class FileListPage(list: List[File], nextPageToken: String) extends FileList
case class CompleteFileList(list: List[File]) extends FileList

object FileList {

  implicit def ListasFileList(listFiles: List[File]): CompleteFileList =  CompleteFileList(listFiles)
  implicit def asListofFiles(fileList: FileList): List[File] = fileList.list

  implicit class AsScalaFileList(fileList: jFileList){
    def asScala : FileList = {
      val pageTokenOpt = Option(fileList.getNextPageToken)
      val listFilesOpt = Option(fileList.getFiles)
        .map{ jList =>
          import collection.JavaConverters._
          jList.asScala.toList
            .map(jFile => jFile.asScala)
        }

      (listFilesOpt, pageTokenOpt) match {
        case (Some(listFiles), Some(pageToken)) =>
          FileListPage(listFiles, pageToken)
        case (Some(listFiles), None) =>
          CompleteFileList(listFiles)
        case (None, Some(pageToken)) =>
          FileListPage(List[File](), pageToken)
        case (None, None) =>
          CompleteFileList(List[File]())
      }

    }
  }

  implicit class AsJavaFileList(fileList: FileList){
    def asJava: jFileList = {
      import collection.JavaConverters._
      val files = fileList.list.map(_.asJava).asJava

      fileList match {
        case _ : CompleteFileList => new jFileList().setFiles(files)
        case FileListPage(_, nextPageToken) => new jFileList().setFiles(files).setNextPageToken(nextPageToken)
      }
    }
  }
}

