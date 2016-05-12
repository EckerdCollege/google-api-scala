package edu.eckerd.google.api.services.directory.models

/**
  * Created by davenpcm on 5/10/16.
  */
case class Groups(
                 groups: Option[List[Group]],
                 nextPageToken: Option[String]
                 )
