package edu.eckerd.google.api.services.directory

import com.typesafe.config.ConfigFactory
import edu.eckerd.google.api.services.Service
import edu.eckerd.google.api.services.Scopes.ADMIN_DIRECTORY

import scala.language.implicitConversions



/**
  * Created by davenpcm on 5/5/16.
  */
case class Directory(serviceAccountEmail: String,
                     impersonatedEmail: String,
                     credentialFilePath: String,
                     applicationName: String,
                     scopes: List[String]
                    ) extends Service {

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

  def apply(): Directory = {

    val config = ConfigFactory.load().getConfig("google")
    val serviceAccountEmail = config.getString("serviceAccountEmail")
    val administratorEmail = config.getString("administratorEmail")
    val applicationName = config.getString("applicationName")
    val credentialFilePath = config.getString("credentialFilePath")
    val scope = ADMIN_DIRECTORY


    apply(serviceAccountEmail,  administratorEmail, credentialFilePath, applicationName, scope)
  }
}

