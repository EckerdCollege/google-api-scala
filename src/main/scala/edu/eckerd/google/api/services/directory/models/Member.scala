package edu.eckerd.google.api.services.directory.models


/**
  * Created by davenpcm on 5/6/16.
  */
case class Member(
                 email: Option[String] = None,
                 id: Option[String] = None,
                 role: String = "MEMBER",
                 memberType: String = "USER"
                 )
