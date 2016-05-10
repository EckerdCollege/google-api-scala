package google.services.admin.directory

import google.language.JavaConverters._
import models.Group
import models.Groups
import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by davenpcm on 5/3/16.
  */
case class groups(directory: Directory) {

  private implicit val service = directory.asJava

  def list(domain: String = "eckerd.edu", resultsPerPage: Int = 500): List[Group] = {
    @tailrec
    def list(
              pageToken: String = "",
              groups: List[Group] = List[Group]()
            ): List[Group] =
    {

      val result = service.groups()
        .list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setPageToken(pageToken)
        .execute().asScala

      val myList = result.groups.getOrElse(List[Group]()) ::: groups

      if (result.nextPageToken.isDefined && result.groups.isDefined) list(result.nextPageToken.get, myList) else myList
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
