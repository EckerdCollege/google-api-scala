package edu.eckerd.google.api.services.calendar

import edu.eckerd.google.api.services.Service

import language.implicitConversions

case class Calendar(serviceAccountEmail: String,
                    impersonatedEmail: String,
                    credentialFilePath: String,
                    applicationName: String,
                    scopes: List[String]
                   ) extends Service {
  val events = new events(this)
}

object Calendar {
  def apply(serviceAccountEmail: String,
            credentialFilePath: String,
            applicationName: String,
            scopes: List[String])(impersonator: String): Calendar = {
    apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, scopes)
  }

  def apply(serviceAccountEmail: String,
            credentialFilePath: String,
            applicationName: String,
            scope: String)(impersonator: String): Calendar = {
    apply(serviceAccountEmail, impersonator, credentialFilePath, applicationName, List(scope))
  }
}
