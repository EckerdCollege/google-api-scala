package google.services.admin.directory.models


/**
  * Created by davenpcm on 5/6/16.
  */
case class Group(
                name: String,
                email: String,
                id: Option[String] = None,
                description: Option[String] = None,
                directMemberCount: Option[Long] = None,
                adminCreated: Option[Boolean] = None
                )

