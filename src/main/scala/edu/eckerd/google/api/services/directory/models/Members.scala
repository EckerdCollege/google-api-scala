package edu.eckerd.google.api.services.directory.models

case class Members(
                  members: Option[List[Member]],
                  nextPageToken: Option[String]
                  )
