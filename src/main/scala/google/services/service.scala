package google.services

import com.google.api.client.googleapis.auth.oauth2.{GoogleClientSecrets, GoogleCredential}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory


/**
  * Created by davenpcm on 5/3/16.
  */
class service {
  /**
    * This function is currently configured for a service account to interface with the school. It has been granted
    * explicitly these grants so to change the scope these need to be implemented in Google first.
    *
    * @return A Google Directory Object which can be used to look through Admin Directory Information
    */
  def getDirectory(googleCredential: GoogleCredential,
                   applicationName: String): com.google.api.services.admin.directory.Directory = {
    val httpTransport = new NetHttpTransport
    val jsonFactory = new JacksonFactory
    val directory = new com.google.api.services.admin.directory.Directory.Builder(httpTransport, jsonFactory, googleCredential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(googleCredential)
      .build()

    directory
  }

  def getCalendar(googleCredential: GoogleCredential, applicationName: String): com.google.api.services.calendar.Calendar = {
    val httpTransport = new NetHttpTransport
    val jsonFactory = new JacksonFactory
    val calendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, googleCredential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(googleCredential)
      .build()

    calendar
  }

  def getDrive(googleCredential: GoogleCredential, applicationName: String): com.google.api.services.drive.Drive = {
    val httpTransport = new NetHttpTransport
    val jsonFactory = new JacksonFactory
    val drive = new com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, googleCredential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(googleCredential)
      .build()

    drive
  }

  def getCredential(serviceAccountEmail: String,
                    impersonatedEmail: String,
                    credentialFilePath: String,
                    applicationName: String,
                    scopes: List[String]): GoogleCredential = {
    import scala.collection.JavaConverters._
    val httpTransport = new NetHttpTransport
    val jsonFactory = new JacksonFactory

    val initCredential = new GoogleCredential.Builder()
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountScopes(scopes.asJavaCollection)
      .setServiceAccountId(serviceAccountEmail)
      .setServiceAccountUser(impersonatedEmail)

    val credential: GoogleCredential = credentialFilePath match {

      case path if path.endsWith(".p12") =>
        val file = new java.io.File(credentialFilePath)
        val credential = initCredential
          .setServiceAccountPrivateKeyFromP12File(file)
          .build()
        credential

      case path if path.endsWith(".json") =>
        val inputStream = this.getClass.getResourceAsStream(credentialFilePath)
        val inputStreamReader = new java.io.InputStreamReader(inputStream)
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, inputStreamReader)
        val credential = initCredential
          .setClientSecrets(clientSecrets)
          .build()
        credential

    }

    if (!credential.refreshToken()) {
      throw new RuntimeException("Failed OAuth to refresh the token")
    }

    credential
  }

}

object service {
  def apply(): service = {
    new service
  }
}
