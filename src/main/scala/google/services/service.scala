package google.services

import com.google.api.client.googleapis.auth.oauth2.{GoogleClientSecrets, GoogleCredential}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory


/**
  * Created by davenpcm on 5/3/16.
  */
class service( serviceAccountEmail: String,
               impersonatedEmail: String,
               credentialFilePath: String,
               applicationName: String,
               scopes: List[String]
             ) {

  val httpTransport = new NetHttpTransport
  val jsonFactory = new JacksonFactory
  val credential = getCredential(serviceAccountEmail, impersonatedEmail, credentialFilePath, applicationName, scopes)

  /**
    * This function is currently configured for a service account to interface with the school. It has been granted
    * explicitly these grants so to change the scope these need to be implemented in Google first.
    *
    * @return A Google Directory Object which can be used to look through Admin Directory Information
    */
  def Directory: com.google.api.services.admin.directory.Directory = {
    val directory = new com.google.api.services.admin.directory.Directory.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(credential)
      .build()

    directory
  }

  /**
    * This function generates a Calendar Service to use To Manipulate Google Calendar
    * @return A google Calendar Service to
    */
  def Calendar: com.google.api.services.calendar.Calendar = {
    val calendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(credential)
      .build()

    calendar
  }

  def Drive: com.google.api.services.drive.Drive = {
    val drive = new com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(applicationName)
      .setHttpRequestInitializer(credential)
      .build()

    drive
  }

  private def getCredential(serviceAccountEmail: String,
                    impersonatedEmail: String,
                    credentialFilePath: String,
                    applicationName: String,
                    scopes: List[String]): GoogleCredential = {
    import scala.collection.JavaConverters._

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
  def apply(serviceAccountEmail: String,
            impersonatedEmail: String,
            credentialFilePath: String,
            applicationName: String,
            scopes: List[String]): service = {
    new service(serviceAccountEmail, impersonatedEmail, credentialFilePath, applicationName, scopes)
  }
}
