package google.services.admin.directory

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.{Member, Members}
import scala.collection.JavaConverters._
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * Created by davenpcm on 5/3/16.
  */
object members {

  /**
    * The function takes a groupKey to return all members of the group. Current Error Handling will terminate on an error
    * from our side, but will retry if it is a bad return response from google, likely a service unavailable. Which
    * occurs sporadically.
    *
    * @param groupKey This is either the unique group id or the group email address. Which specifies which group to
    *                 return the members of
    * @param service This is the service we will be utilizing to get the members. It is initialized as a Parameter as
    *                that way it is only initialized on entry to the loop and then the same directory is used each time
    * @param pageToken This is the page token that shows where we are in the pages.
    * @param members This is the growing list of group members
    * @return A list of all members of the group
    */
  @tailrec
  def list(groupKey: String,
                          service: Directory,
                          pageToken: String = "",
                          members: List[Member] = List[Member]()
                         ): List[Member] = {

    val resultTry = Try(
      service.members()
        .list(groupKey)
        .setMaxResults(500)
        .setPageToken(pageToken)
        .execute()
    )

    resultTry match {
      case Success(result) =>
        val typedList = List[Members](result)
          .map(members => Option(members.getMembers))
          .map{ case Some(member) => member.asScala.toList case None => List[Member]() }
          .foldLeft(List[Member]())((acc, listMembers) => listMembers ::: acc)

        val myList = typedList ::: members

        val nextPageToken = result.getNextPageToken

        if (nextPageToken != null && result.getMembers != null) list(groupKey, service, nextPageToken, myList) else myList

      case Failure(exception: GoogleJsonResponseException) => list(groupKey, service, pageToken, members)
      case Failure(e) => throw e
    }

  }

  def AddUserToGroup(groupKey: String, id: String,
                     service: Directory
                    ): Member = {

    val newMember = new Member
    newMember.setId(id)
    newMember.setRole("MEMBER")

    val InsertedMember = service.members().insert(groupKey, newMember).execute()
    InsertedMember
  }

  def RemoveUserFromGroup(groupKey: String,
                          userId: String,
                          service: Directory
                         ): Unit = {
    service.members().delete(groupKey, userId).execute()
  }

}
