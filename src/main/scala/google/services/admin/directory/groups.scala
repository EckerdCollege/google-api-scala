package google.services.admin.directory

import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.model.{Group, Groups}
import collection.JavaConverters._
import scala.annotation.tailrec

/**
  * Created by davenpcm on 5/3/16.
  */
object groups {

  /**
    * This function simply returns a list of all groups in the organization.
    *
    * @param service The Directory Service We Are Getting the Groups From
    * @param pageToken The Page Token for the current page. We Initialize As An Empty String and automatically fill it
    *                  out as we move through the pages
    * @param groups This is the growing list of groups that is recursively passed through the function
    * @return A list of all groups.
    */
  @tailrec
  def list(service: Directory,
                    pageToken: String = "",
                    groups: List[Group] = List[Group]()
                   ): List[Group] ={

    val result = service.groups()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[Groups](result)
      .map(groups => groups.getGroups.asScala.toList)
      .foldLeft(List[Group]())((acc, listGroups) => listGroups ::: acc)

    val mylist = typedList ::: groups

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getGroups != null) list(service, nextPageToken, mylist) else mylist
  }

  def create(group: Group,
             service: Directory): Group = {
    service.groups().insert(group).execute()
  }

  def delete(groupKey: String,
             service: Directory
            ): Unit = {
    service.groups().delete(groupKey).execute()
  }

}
