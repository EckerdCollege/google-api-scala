package google.services.admin.directory

import google.services.admin.directory.models.Member
import google.services.admin.directory.models.Group

import scala.annotation.tailrec
import google.services.JavaConverters._
import language.implicitConversions
import language.postfixOps

/**
  * Created by davenpcm on 5/3/16.
  */
class members(directory: Directory) {

  private val service = directory.asJava

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
        .foldLeft(List[Member]())((acc, listMembers) => listMembers.map(_.asScala) ::: acc)

      val myList = typedList ::: members

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getMembers != null) list(nextPageToken, myList)
      else myList
    }
    list()
  }

  def addById(groupKey: String, newMemberId: String): Member = {

    val newMember = Member(None, Some(newMemberId))

    val InsertedMember = service.members().insert(groupKey, newMember.asJava).execute()
    InsertedMember.asScala
  }

  def add(groupKey: String, newMemberEmail: String): Member = {
    val newMember = Member(Some(newMemberEmail))
    val InsertedMember = service.members().insert(groupKey, newMember.asJava).execute()
    InsertedMember.asScala
  }

  def add(groupKey: String, newMember: Member): Member = {
    val InsertedMember = service.members().insert(groupKey, newMember.asJava).execute()
    InsertedMember.asScala
  }

  def add(group: Group, newMember: Member): Member = {
    val InsertedMember = if ( group.id.isDefined){
      service.members().insert(group.id.get, newMember.asJava).execute()
    } else {
      service.members().insert(group.email, newMember.asJava).execute()
    }

    InsertedMember.asScala
  }

  def remove(groupKey: String, userId: String): Unit = {
    service.members().delete(groupKey, userId).execute()
  }

}
