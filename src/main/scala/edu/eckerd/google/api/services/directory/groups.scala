package edu.eckerd.google.api.services.directory

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import edu.eckerd.google.api.language.JavaConverters._
import models.Group

import scala.annotation.tailrec
import scala.util.Try

/**
  * Created by davenpcm on 5/3/16.
  */
class groups(directory: Directory) {

  private implicit val service = directory.asJava

  def list(domain: String = "eckerd.edu", resultsPerPage: Int = 500): List[Group] = {
    @tailrec
    def list(pageToken: String = "", groups: List[Group] = List[Group]()): List[Group] = {
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
    Try(service.groups().get(identifier).execute()).map(_.asScala) recoverWith {
      case e: GoogleJsonResponseException if e.getMessage contains "Request rate higher than configured." =>
        Thread.sleep(100)
        get(identifier)
    }
  }

  def get(group: Group): Try[Group] = {
    Try{
      service.groups().get(group.email).execute()
    }.map(_.asScala) recoverWith {
      case e: GoogleJsonResponseException if e.getMessage contains "Request rate higher than configured." =>
        Thread.sleep(100)
        get(group)
    }
  }

//  def get(group: Group, domain: String = "eckerd.edu", resultsPerPage: Int = 500): Option[Group] = {
//
//    @tailrec
//    def list(pageToken: String = ""): Option[Group] = {
//      val result = service.groups()
//        .list()
//        .setDomain(domain)
//        .setMaxResults(resultsPerPage)
//        .setPageToken(pageToken)
//        .execute().asScala
//
//      val myList = result.groups.getOrElse(List[Group]())
//
//      val matching = for (listedGroup <- myList if listedGroup.name == group.name || listedGroup.email == group.email )
//        yield listedGroup
//
//      val matched = matching.headOption
//
//      if (result.nextPageToken.isDefined && result.groups.isDefined && matched.isEmpty) list(result.nextPageToken.get) else matched
//    }
//    list()
//  }

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
