package google.services.drive

import google.services.Service

import language.implicitConversions

/**
  * Created by davenpcm on 5/5/16.
  */
case class Drive(serviceAccountEmail: String,
                 impersonatedEmail: String,
                 credentialFilePath: String,
                 applicationName: String,
                 scopes: List[String]
                ) extends Service(serviceAccountEmail, impersonatedEmail, credentialFilePath, applicationName, scopes) {

  val files = new files(this)
  val permissions = new permissions(this)

}

object Drive {
  def apply(serviceAccountEmail: String,
            credentialFilePath: String,
            applicationName: String,
            scopes: List[String])(impersonator: String): Drive = {
    apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, scopes)
  }

  def apply(serviceAccountEmail: String,
            credentialFilePath: String,
            applicationName: String,
            scope: String)(impersonator: String): Drive = {
    apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, List(scope))
  }
}