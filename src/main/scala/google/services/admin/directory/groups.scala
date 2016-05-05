package google.services.admin.directory


import com.google.api.services.admin.directory.model.{Group, Groups}
import collection.JavaConverters._
import scala.annotation.tailrec

/**
  * Created by davenpcm on 5/3/16.
  */
case class groups(directory: Directory) {
  val service = directory.directory

  /**
    * This function simply returns a list of all groups in the organization.
    *
    * @param pageToken The Page Token for the current page. We Initialize As An Empty String and automatically fill it
    *                  out as we move through the pages
    * @param groups This is the growing list of groups that is recursively passed through the function
    * @return A list of all groups.
    */
  @tailrec
  final def list(
           domain: String = "eckerd.edu",
           resultsPerPage: Int = 500,
           pageToken: String = "",
           groups: List[Group] = List[Group]()
          ): List[Group] ={

    val result = service.groups()
      .list()
      .setDomain(domain)
      .setMaxResults(resultsPerPage)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[Groups](result)
      .map(groups => groups.getGroups.asScala.toList)
      .foldLeft(List[Group]())((acc, listGroups) => listGroups ::: acc)

    val myList = typedList ::: groups

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getGroups != null) list(domain, resultsPerPage, nextPageToken, myList) else myList
  }

  /**
    * This creates a google group
    * @param group This is the group to be created
    * @return The group created with all the information google has filled in on top what was originally entered.
    */
  def create(group: Group): Group = {
    service.groups().insert(group).execute()
  }

  /**
    * Deletes A Google Group
    * @param groupKey The Key of the group either the email adress or the unique key
    */
  def delete(groupKey: String): Unit = {
    service.groups().delete(groupKey).execute()
  }

}
