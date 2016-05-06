package google.services.admin.directory.models

import scala.language.implicitConversions
import scala.language.postfixOps
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

object Group {

  implicit def toGoogleApi(group: Group): com.google.api.services.admin.directory.model.Group = {
    val newGroup = new com.google.api.services.admin.directory.model.Group()
      .setName(group.name)
      .setEmail(group.email)

    if (group.id isDefined) { newGroup.setId(group.id.get) }
    if (group.description isDefined) { newGroup.setDescription(group.description.get) }
    if (group.directMemberCount isDefined){ newGroup.setDirectMembersCount(group.directMemberCount.get)}
    if (group.adminCreated isDefined){ newGroup.setAdminCreated(group.adminCreated.get)}

    newGroup
  }

  implicit def apply(group: com.google.api.services.admin.directory.model.Group): Group = {
    Group(
      group.getName,
      group.getEmail,
      Option(group.getId),
      Option(group.getDescription),
      Option(group.getDirectMembersCount),
      Option(group.getAdminCreated)
    )
  }

}
