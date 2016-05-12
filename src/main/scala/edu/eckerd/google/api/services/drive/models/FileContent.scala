package edu.eckerd.google.api.services.drive.models

import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/5/16.
  */
case class FileContent(fullFilePath: String, mimeType: String) {
 val content = new java.io.File(fullFilePath)
}
