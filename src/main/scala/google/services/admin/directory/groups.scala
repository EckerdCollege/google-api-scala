package google.services.admin.directory

import google.services.admin.directory.models.Group
import scala.language.implicitConversions
import scala.language.postfixOps
import scala.annotation.tailrec

/**
  * Created by davenpcm on 5/3/16.
  */
case class groups(directory: Directory) {

  private val service: com.google.api.services.admin.directory.Directory = directory

  private implicit def toGoogleApi(group: Group): com.google.api.services.admin.directory.model.Group = {
    val newGroup = new com.google.api.services.admin.directory.model.Group()
      .setName(group.name)
      .setEmail(group.email)

    if (group.id isDefined) { newGroup.setId(group.id.get) }
    if (group.description isDefined) { newGroup.setDescription(group.description.get) }
    if (group.directMemberCount isDefined){ newGroup.setDirectMembersCount(group.directMemberCount.get)}
    if (group.adminCreated isDefined){ newGroup.setAdminCreated(group.adminCreated.get)}

    newGroup
  }

  private implicit def fromGoogleApi(group: com.google.api.services.admin.directory.model.Group): Group = {
    Group(
      group.getName,
      group.getEmail,
      Option(group.getId),
      Option(group.getDescription),
      Option(group.getDirectMembersCount),
      Option(group.getAdminCreated)
    )
  }

  def list(domain: String = "eckerd.edu", resultsPerPage: Int = 500): List[Group] = {
    @tailrec
    def list(
              pageToken: String = "",
              groups: List[Group] = List[Group]()
            ): List[Group] =
    {
      import com.google.api.services.admin.directory.model.Groups
      import collection.JavaConverters._

      val result = service.groups()
        .list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setPageToken(pageToken)
        .execute()

      val typedList = List[Groups](result)
        .map(groups => groups.getGroups.asScala.toList)
        .foldLeft(List[Group]())((acc, listGroups) => listGroups.map(fromGoogleApi) ::: acc)

      val myList = typedList ::: groups

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getGroups != null) list(nextPageToken, myList) else myList
    }

    list()
  }

  /**
    * This creates a google group
    *
    * @param group This is the group to be created
    * @return The group created with all the information google has filled in on top what was originally entered.
    */
  def create(group: Group): Group = {
    service.groups().insert(group).execute()
  }

  /**
    * Deletes A Google Group
    *
    * @param groupKey The Key of the group either the email adress or the unique key
    */
  def delete(groupKey: String): Unit = {
    service.groups().delete(groupKey).execute()
  }

}
