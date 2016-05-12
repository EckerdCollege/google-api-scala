package edu.eckerd.google.api.services.drive.models


/**
  * Created by davenpcm on 5/4/16.
  */
case class Permission(
                      role: String,
                      permissionType: String,
                      emailAddress: Option[String] = None,
                      displayName: Option[String] = None,
                      id: Option[String] = None
                     )
