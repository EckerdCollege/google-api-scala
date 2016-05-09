package google.services.admin.directory

import google.language.JavaConverters._
import models.Group

import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by davenpcm on 5/3/16.
  */
case class groups(directory: Directory) {

  private val service = directory.asJava

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
        .foldLeft(List[Group]())((acc, listGroups) => listGroups.map(_.asScala) ::: acc)

      val myList = typedList ::: groups

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getGroups != null) list(nextPageToken, myList) else myList
    }

    list()
  }

  def get(identifier: String ): Try[Group] ={
    Try(service.groups().get(identifier).execute()).map(_.asScala)
  }

  /**
    * This creates a google group
    *
    * @param group This is the group to be created
    * @return The group created with all the information google has filled in on top what was originally entered.
    */
  def create(group: Group): Group = {
    service.groups().insert(group.asJava).execute().asScala
  }

  /**
    * Deletes A Google Group
    *
    * @param groupKey The Key of the group either the email adress or the unique key
    */
  def delete(groupKey: String): Try[Unit] = {
    Try(service.groups().delete(groupKey).execute())
  }

}
