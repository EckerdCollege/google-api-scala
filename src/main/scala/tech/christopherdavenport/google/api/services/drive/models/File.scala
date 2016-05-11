package tech.christopherdavenport.google.api.services.drive.models


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
               )