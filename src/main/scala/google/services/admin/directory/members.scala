package google.services.admin.directory

import google.services.admin.directory.models.Member
import google.services.admin.directory.models.Group

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import language.implicitConversions
import language.postfixOps

/**
  * Created by davenpcm on 5/3/16.
  */
class members(directory: Directory) {

  private val service: com.google.api.services.admin.directory.Directory = directory

  private implicit def toGoogleApi(member: Member): com.google.api.services.admin.directory.model.Member = {
    val newMember = new com.google.api.services.admin.directory.model.Member()
      .setRole(member.role)
      .setType(member.memberType)

    if (member.email isDefined){ newMember.setEmail(member.email.get)}
    if (member.id isDefined){ newMember.setId(member.id.get)}

    newMember
  }

  private implicit def fromGoogleApi(member: com.google.api.services.admin.directory.model.Member): Member = {
    Member(
      Option(member.getEmail),
      Option(member.getId),
      member.getRole,
      member.getType
    )
  }

  /**
    * The function takes a groupKey to return all members of the group. Current Error Handling will terminate on an error
    * from our side, but will retry if it is a bad return response from google, likely a service unavailable. Which
    * occurs sporadically.
    *
    * @param groupKey This is either the unique group id or the group email address. Which specifies which group to
    *                 return the members of
    * @return A list of all members of the group
    */
  def list(groupKey: String): List[Member] = {
    @tailrec
    def list(
             pageToken: String = "",
             members: List[Member] = List[Member]()
            ): List[Member] = {
      import scala.collection.JavaConverters._
      import com.google.api.services.admin.directory.model.Members

      val result = service.members()
        .list(groupKey)
        .setMaxResults(500)
        .setPageToken(pageToken)
        .execute()

      val typedList = List[Members](result)
        .map(members =>
          if (Option(members.getMembers).isDefined) {
            members.getMembers.asScala.toList
          }
          else {
            List[com.google.api.services.admin.directory.model.Member]()
          }
        )
        .foldLeft(List[Member]())((acc, listMembers) => listMembers.map(fromGoogleApi) ::: acc)

      val myList = typedList ::: members

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getMembers != null) list(nextPageToken, myList)
      else myList
    }
    list()
  }

  def addById(groupKey: String, newMemberId: String): Member = {

    val newMember = Member(None, Some(newMemberId))

    val InsertedMember = service.members().insert(groupKey, newMember).execute()
    InsertedMember
  }

  def add(groupKey: String, newMemberEmail: String): Member = {
    val newMember = Member(Some(newMemberEmail))
    val InsertedMember = service.members().insert(groupKey, newMember).execute()
    InsertedMember
  }

  def add(groupKey: String, newMember: Member): Member = {
    val InsertedMember = service.members().insert(groupKey, newMember).execute()
    InsertedMember
  }

  def add(group: Group, newMember: Member): Member = {
    val InsertedMember = if ( group.id.isDefined){
      service.members().insert(group.id.get, newMember).execute()
    } else {
      service.members().insert(group.email, newMember).execute()
    }

    InsertedMember
  }

  def remove(groupKey: String, userId: String): Unit = {
    service.members().delete(groupKey, userId).execute()
  }

}
