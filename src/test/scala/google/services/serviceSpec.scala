package google.services

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConverters._

/**
  * Created by davenpcm on 5/5/16.
  */

class serviceSpec extends FlatSpec with Matchers {

  val config = ConfigFactory.load().getConfig("googleTest")
  val serviceAccountEmail = config.getString("email")
  val credentialFilePath = config.getString("pkcs12FilePath")
  val applicationName = config.getString("applicationName")
  val adminImpersonatedEmail = config.getString("impersonatedEmail")

  val ListScopes = List(
    DirectoryScopes.ADMIN_DIRECTORY_USER,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER,
    CalendarScopes.CALENDAR,
    DriveScopes.DRIVE,
    DriveScopes.DRIVE_APPDATA,
    GmailScopes.GMAIL_COMPOSE
  )

  val service = google.services.service(serviceAccountEmail,
    adminImpersonatedEmail,
    credentialFilePath,
    applicationName,
    ListScopes
  )

  val credential = service.credential

  "A Google Credential" should "be returned from getCredential" in {
    credential shouldBe a [GoogleCredential]
  }

  it should "have the service account Email from the Configuration" in {
    credential.getServiceAccountUser === serviceAccountEmail
  }

  it should "have the same scope as what was requested" in {
    credential.getServiceAccountScopes === ListScopes.asJava
  }

  it should "have a service account Id match the email" in {
    credential.getServiceAccountId === serviceAccountEmail
  }

}
