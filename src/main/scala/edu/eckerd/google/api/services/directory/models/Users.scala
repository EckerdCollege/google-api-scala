package edu.eckerd.google.api.services.directory.models

case class Users(
                users: Option[List[User]],
                nextPageToken: Option[String]
                )
