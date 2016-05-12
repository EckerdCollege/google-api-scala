package edu.eckerd.google.api.language

import com.google.api.services.admin.{directory => jDirectory}
import com.google.api.services.{calendar => jCalendar, drive => jDrive}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}
import edu.eckerd.google.api.services.{directory => sDirectory}
import edu.eckerd.google.api.services.{calendar => sCalendar, drive => sDrive}
import edu.eckerd.google.api.services.Scopes._



/**
  * Created by davenpcm on 5/11/16.
  */
class javaConversionsSpec extends FlatSpec with Matchers {

  val config = ConfigFactory.load().getConfig("googleTest")
  val applicationName = config.getString("applicationName")
  val credentialFilePath = config.getString("credentialFilePath")
  val serviceAccountEmail = config.getString("serviceAccountEmail")
  val adminImpersonatedEmail = config.getString("administratorEmail")

  val ListScopes = CALENDAR :: ADMIN_DIRECTORY ::: DRIVE

  "scalaDirectoryAsJavaDirectoryConversion" should "convert a scala Directory to a Java Directory" in {
    val adminDir = sDirectory.Directory(serviceAccountEmail,
      adminImpersonatedEmail,
      credentialFilePath,
      applicationName,
      ListScopes
    )
    val jDir = JavaConversions.scalaDirectoryAsJavaDirectoryConversion(adminDir)
    jDir shouldBe a [jDirectory.Directory]
  }

  it should "maintain the application name through conversion" in {
    val adminDir = sDirectory.Directory(serviceAccountEmail,
      adminImpersonatedEmail,
      credentialFilePath,
      applicationName,
      ListScopes
    )
    val jDir = JavaConversions.scalaDirectoryAsJavaDirectoryConversion(adminDir)
    jDir.getApplicationName === applicationName
  }

  "scalaGroupAsJavaGroupConversion" should "convert a scala group to java group" in {
    val group = sDirectory.models.Group("TestGroup", "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup shouldBe a [jDirectory.model.Group]
  }

  it should "maintain the group name through conversion" in {
    val name = "TestGroup"
    val group = sDirectory.models.Group(name, "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getName === name
  }

  it should "maintain the email through conversion" in {
    val email = "test@test.com"
    val group = sDirectory.models.Group("TestGroup", email)
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getEmail === email
  }

  it should "convert a id of None to null" in {
    val group = sDirectory.models.Group("TestGroup", "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getId === null
  }

  it should "convert an id of Some(string) to string" in {
    val string = "115242516671582"
    val group = sDirectory.models.Group("TestGroup", "test@test.com", id = Some(string))
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getId === string

  }

  it should "convert a description of None to null" in {
    val group = sDirectory.models.Group("TestGroup", "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getDescription === null
  }

  it should "convert a description of Some(string) to string" in {
    val string = "Best Group Ever"
    val group = sDirectory.models.Group("TestGroup", "test@test.com", description = Some(string))
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getDescription === string
  }

  it should "convert a directMembersCount of None to null" in {
    val group = sDirectory.models.Group("TestGroup", "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getDirectMembersCount === null
  }

  it should "convert a directMembersCount of Some(long) to long" in {
    val long = 3L
    val group = sDirectory.models.Group("TestGroup", "test@test.com", directMemberCount = Some(long))
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getDirectMembersCount === long
  }

  it should "convert adminCreated of None to null" in {
    val group = sDirectory.models.Group("TestGroup", "test@test.com")
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getAdminCreated === null
  }

  it should "convert adminCreated of Some(bool) to bool" in {
    val bool = true
    val group = sDirectory.models.Group("TestGroup", "test@test.com", adminCreated = Some(bool))
    val jGroup = JavaConversions.scalaGroupAsJavaGroupConversion(group)
    jGroup.getAdminCreated === bool
  }

  "javaGroupAsScalaGroupConversion" should "return a Scala Group" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup shouldBe a [sDirectory.models.Group]
  }

  it should "maintain the name through conversion" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.name === name
  }

  it should "throw an error if the name is blank" in {
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setEmail(email)
    intercept[Throwable]{
      JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    }
  }

  it should "maintain the email through conversion" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.email === email
  }

  it should "throw an error if the email is blank" in {
    val name = "TestGroup"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
    intercept[Throwable]{
      JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    }
  }

  it should "return a None if the id is null" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.id === None
  }

  it should "return Some(id) if the id is set" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val id = "11514754716654q1a"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
      .setId(id)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.id.get === id
  }

  it should "return a None if the description is null" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.description === None
  }

  it should "return Some(description) if the description is set" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val description = "Best Group Ever"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
      .setDescription(description)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.description.get === description
  }

  it should "return a None if the members count is null" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.directMemberCount === None
  }

  it should "return Some(long) if the members count is set" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val count = 3L
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
      .setDirectMembersCount(count)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.directMemberCount.get === count
  }

  it should "return a None for Members" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.members === None
  }

  it should "return a None if the Admin Created is null" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.adminCreated === None
  }

  it should "return Some(bool) if the Admin Created is set" in {
    val name = "TestGroup"
    val email = "test@test.com"
    val bool = true
    val javaGroup = new jDirectory.model.Group()
      .setName(name)
      .setEmail(email)
      .setAdminCreated(bool)
    val sGroup = JavaConversions.javaGroupAsScalaGroupConversion(javaGroup)
    sGroup.adminCreated.get === bool
  }

}
