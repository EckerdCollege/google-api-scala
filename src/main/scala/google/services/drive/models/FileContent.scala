package google.services.drive.models

import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/5/16.
  */
case class FileContent(fullFilePath: String, mimeType: String) {
 val content = new java.io.File(fullFilePath)

}

object FileContent{
  implicit def toGoogleApi(fileContent: FileContent): com.google.api.client.http.FileContent = {
    new com.google.api.client.http.FileContent(fileContent.mimeType, fileContent.content)
  }


}
