package tech.christopherdavenport.google.api.services.admin.directory.models

import tech.christopherdavenport.google.api.services.admin.directory.Directory


/**
  * Created by davenpcm on 5/6/16.
  */
case class Group(
                name: String,
                email: String,
                id: Option[String] = None,
                description: Option[String] = None,
                directMemberCount: Option[Long] = None,
                members: Option[List[Member]] = None,
                adminCreated: Option[Boolean] = None
                ){
  def getMembers(implicit directory: Directory): Group = {
    val groupMembers = directory.members.list(this.id.get)
    this.copy(members = Option(groupMembers))
  }
}

object Group {

}

