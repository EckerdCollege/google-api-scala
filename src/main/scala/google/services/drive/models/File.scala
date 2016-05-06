package google.services.drive.models

import language.implicitConversions
import language.postfixOps
/**
  * Created by davenpcm on 5/5/16.
  */
case class File(name: String,
                mimeType: String,
                id: Option[String] = None,
                extension: Option[String] = None,
                description: Option[String] = None,
                parentIds: Option[List[String]] = None,
                content: Option[FileContent]= None
               ){


}

object File {
  implicit def toGoogleApi(file: File): com.google.api.services.drive.model.File = {
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

  implicit def fromGoogleApi(file: com.google.api.services.drive.model.File): File = {
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

}
