package tech.christopherdavenport.google.api.services.admin.directory.models

/**
  * Created by davenpcm on 5/10/16.
  */
case class Groups(
                 groups: Option[List[Group]],
                 nextPageToken: Option[String]
                 )
