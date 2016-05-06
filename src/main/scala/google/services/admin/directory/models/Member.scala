package google.services.admin.directory.models

import language.implicitConversions
import language.postfixOps
/**
  * Created by davenpcm on 5/6/16.
  */
case class Member(
                 email: Option[String] = None,
                 id: Option[String] = None,
                 role: String = "MEMBER",
                 memberType: String = "USER"
                 )

object Member {
  implicit def toGoogleApi(member: Member): com.google.api.services.admin.directory.model.Member = {
    val newMember = new com.google.api.services.admin.directory.model.Member()
      .setRole(member.role)
      .setType(member.memberType)

    if (member.email isDefined){ newMember.setEmail(member.email.get)}
    if (member.id isDefined){ newMember.setId(member.id.get)}

    newMember
  }

  implicit def apply(member: com.google.api.services.admin.directory.model.Member): Member = {
    Member(
      Option(member.getEmail),
      Option(member.getId),
      member.getRole,
      member.getType
    )
  }



}
