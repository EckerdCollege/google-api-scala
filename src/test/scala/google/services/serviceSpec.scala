package google.services

import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import google.services.admin.directory.Directory
import google.services.drive.Drive
import google.services.calendar.Calendar
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._
import google.services.Service

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

  def serviceFixture = Service(serviceAccountEmail,
      adminImpersonatedEmail,
      credentialFilePath,
      applicationName,
      ListScopes
    )

  def partialServiceFixture = Service(serviceAccountEmail, credentialFilePath,applicationName,ListScopes)(_)


  "A Service" should "be returned from creating new service" in {
    val s = serviceFixture
    s shouldBe a [Service]
  }

  it should "throw an error on a nonsense user" in {
    val nonsense = "kajsdf91234k;jefasdf"
    intercept[TokenResponseException]{
      Service(serviceAccountEmail, nonsense, credentialFilePath, applicationName, ListScopes)
    }
  }

  it should "have a JacksonFactory" in {
    val s = serviceFixture
    s.jsonFactory shouldBe a [JacksonFactory]
  }

  it should "have a transport layer" in {
    val s = serviceFixture
    s.httpTransport shouldBe a [NetHttpTransport]
  }

  it should "be able to generate a Directory" in {
    val s = serviceFixture
    s.Directory shouldBe a [Directory]
  }

  it should "be able to generate a Drive" in {
    val s = serviceFixture
    s.Drive shouldBe a [Drive]
  }

  it should "be able to generate a Calendar" in {
    val s = serviceFixture
    s.Calendar shouldBe a [Calendar]
  }

  "A Credential" should "be contained in a new service" in {
    val s = serviceFixture
    s.credential shouldBe a [GoogleCredential]
  }

  it should "have the service account Email from the Configuration" in {
    val s = serviceFixture
    s.credential.getServiceAccountUser === serviceAccountEmail
  }

  it should "have the same scope as what was requested" in {
    val s = serviceFixture
    s.credential.getServiceAccountScopes === ListScopes.asJava
  }

  it should "have a service account Id match the email" in {
    val s = serviceFixture
    s.credential.getServiceAccountId === serviceAccountEmail
  }

  "The Companion Object" should "be able to generate a partial function for impersonating users" in {
    val f = partialServiceFixture
    f shouldBe a [(String => Service)]
  }

  it should "be able to generate a Service" in {
    val f = partialServiceFixture
    f(adminImpersonatedEmail) shouldBe a [Service]
  }

  it should "throw and error on a nonsense impersonation email" in {
    val f = partialServiceFixture
    intercept[TokenResponseException]{
      f("askjdfaksdf;jask23")
    }
  }

  it should "be able to generate an identical service as a full apply" in {
    val f = partialServiceFixture
    f(adminImpersonatedEmail) === serviceFixture
  }

}
