package google.services.admin.directory
import akka.actor.{Actor, ActorSystem, Props}
import google.services.Service

import scala.language.implicitConversions
import akka.pattern

/**
  * Created by davenpcm on 5/5/16.
  */
case class Directory(serviceAccountEmail: String,
                     impersonatedEmail: String,
                     credentialFilePath: String,
                     applicationName: String,
                     scopes: List[String]
                    ) extends Service(
  serviceAccountEmail,
  impersonatedEmail,
  credentialFilePath,
  applicationName,
  scopes) {

//  val actorSystem = ActorSystem(impersonatedEmail+"Dir")

//  val throttler = actorSystem.actorOf(Props(classOf[TimerBasedThrottler], 3 msgsPer 1.second)


  val groups = new groups(this)
  val members = new members(this)
  val photos = new photos(this)
  val users = new users(this)
}

object Directory {
    def apply(serviceAccountEmail: String,
              credentialFilePath: String,
              applicationName: String,
              scopes: List[String])(impersonator: String): Directory = {
      apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, scopes)
    }

    def apply(serviceAccountEmail: String,
              credentialFilePath: String,
              applicationName: String,
              scope: String)(impersonator: String): Directory = {
      apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, List(scope))
    }
}

