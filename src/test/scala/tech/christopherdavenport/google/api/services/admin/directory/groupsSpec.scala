package tech.christopherdavenport.google.api.services.admin.directory

import org.scalatest.{FlatSpec, Matchers}
import tech.christopherdavenport.google.api.services.Scopes._
import tech.christopherdavenport.google.api.services.admin.directory.Directory
import com.typesafe.config.ConfigFactory

/**
  * Created by davenpcm on 5/5/16.
  */
class groupsSpec extends FlatSpec with Matchers {

  val config = ConfigFactory.load().getConfig("googleTest")
  val serviceAccountEmail = config.getString("email")
  val credentialFilePath = config.getString("pkcs12FilePath")
  val applicationName = config.getString("applicationName")
  val adminImpersonatedEmail = config.getString("impersonatedEmail")

  val ListScopes = ADMIN_DIRECTORY

  def groupFixture = Directory(serviceAccountEmail,
    adminImpersonatedEmail,
    credentialFilePath,
    applicationName,
    ListScopes
  ).groups

  "Groups list" should "return a List[Group]" in {
    val g1 = groupFixture
    g1.list() shouldBe a [List[_]]
  }



}
