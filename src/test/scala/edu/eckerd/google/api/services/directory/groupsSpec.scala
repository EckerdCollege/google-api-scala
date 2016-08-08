package edu.eckerd.google.api.services.directory

import org.scalatest.{FlatSpec, Matchers}
import com.typesafe.config.ConfigFactory
import edu.eckerd.google.api.services.Scopes.ADMIN_DIRECTORY

/**
  * Created by davenpcm on 5/5/16.
  */
//class groupsSpec extends FlatSpec with Matchers {

//  val config = ConfigFactory.load().getConfig("googleTest")
//  val serviceAccountEmail = config.getString("serviceAccountEmail")
//  val credentialFilePath = config.getString("credentialFilePath")
//  val applicationName = config.getString("applicationName")
//  val adminImpersonatedEmail = config.getString("administratorEmail")
//
//  val ListScopes = ADMIN_DIRECTORY
//
//  def groupFixture = Directory(serviceAccountEmail,
//    adminImpersonatedEmail,
//    credentialFilePath,
//    applicationName,
//    ListScopes
//  ).groups
//
//  "Groups list" should "return a List[Group]" in {
//    val g1 = groupFixture
//    g1.list() shouldBe a [List[_]]
//  }




//}
