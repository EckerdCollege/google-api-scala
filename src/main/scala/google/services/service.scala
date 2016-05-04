package google.services

import java.io.FileInputStream

import com.google.api.client.googleapis.auth.oauth2.{GoogleClientSecrets, GoogleCredential}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.Directory
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters

/**
  * Created by davenpcm on 5/3/16.
  */
object service {
  /**
    * This function is currently configured for a service account to interface with the school. It has been granted
    * explicitly these grants so to change the scope these need to be implemented in Google first.
    *
    * @param DirectoryScope This is the scope that is requested for whatever operations you would like to perform.
    * @return A Google Directory Object which can be used to look through Admin Directory Information
    */
  def getDirectory(DirectoryScope:String): com.google.api.services.admin.directory.Directory = {
    import java.util._

    val conf = ConfigFactory.load().getConfig("google")
    val SERVICE_ACCOUNT_EMAIL = conf.getString("email")
    val SERVICE_ACCOUNT_PKCS12_FILE_PATH = conf.getString("pkcs12FilePath")
    val APPLICATION_NAME = conf.getString("applicationName")
    val USER_EMAIL = conf.getString("impersonatedEmail")

    val httpTransport = new NetHttpTransport
    val jsonFactory = new JacksonFactory

    val credential = new GoogleCredential.Builder()
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
      .setServiceAccountScopes( Collections.singleton( DirectoryScope ) )
      .setServiceAccountPrivateKeyFromP12File(
        new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)
      )
      .setServiceAccountUser(USER_EMAIL)
      .build()

    if (!credential.refreshToken()) {
      throw new RuntimeException("Failed OAuth to refresh the token")
    }

    val directory = new com.google.api.services.admin.directory.Directory.Builder(httpTransport,
      jsonFactory,
      credential)
      .setApplicationName(APPLICATION_NAME)
      .setHttpRequestInitializer(credential)
      .build()

    directory
  }

  def getCalendar(Scope:String,
                  USER_EMAIL: String): com.google.api.services.calendar.Calendar
  = {
    import java.util._

    val conf = ConfigFactory.load().getConfig("google")
    val SERVICE_ACCOUNT_EMAIL = conf.getString("email")
    val SERVICE_ACCOUNT_PKCS12_FILE_PATH = conf.getString("pkcs12FilePath")
    val APPLICATION_NAME = conf.getString("applicationName")


    val httpTransport = new NetHttpTransport()
    val jsonFactory = new JacksonFactory

    val credential = new GoogleCredential.Builder()
      .setTransport( httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
      .setServiceAccountScopes( Collections.singleton( Scope ) )
      .setServiceAccountPrivateKeyFromP12File(
        new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)
      )
      .setServiceAccountUser(USER_EMAIL)
      .build()

    if (!credential.refreshToken()) {
      throw new RuntimeException("Failed OAuth to refresh the token")
    }

    val calendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(APPLICATION_NAME)
      .setHttpRequestInitializer(credential)
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

    val initialCred = new GoogleCredential.Builder()
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountScopes(scopes.asJava)
      .setServiceAccountUser(impersonatedEmail)


    val credential: GoogleCredential = credentialFilePath match {
      case path if path.endsWith(".p12") =>
        val file = new java.io.File(credentialFilePath)
        val credential = initialCred
          .setServiceAccountPrivateKeyFromP12File(file)
          .build()
        credential

      case path if path.endsWith(".json") =>
        val inputStream = this.getClass.getResourceAsStream(credentialFilePath)
        val inputStreamReader = new java.io.InputStreamReader(inputStream)
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, inputStreamReader)
        val credential = initialCred
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
