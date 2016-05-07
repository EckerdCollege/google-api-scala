package google.services

import google.services.admin.{directory => sDirectory}
import com.google.api.services.admin.{directory => jDirectory}
import google.services.{drive => sDrive}
import com.google.api.services.{drive => jDrive}

import JavaConversions._
import scala.language.implicitConversions
import scala.language.postfixOps

/**
  * Created by davenpcm on 5/7/16.
  */
object JavaConverters {

  class AsJava[C](op: => C){
    def asJava: C = op
  }

  class AsScala[C](op: => C){
    def asScala: C = op
  }

  implicit def scalaDirectoryAsJavaDirectoryConverter(b: sDirectory.Directory): AsJava[jDirectory.Directory] = {
    new AsJava(scalaDirectoryAsJavaDirectoryConversion(b))
  }

  implicit def scalaGroupAsJavaGroupConverter(b: sDirectory.models.Group): AsJava[jDirectory.model.Group] = {
    new AsJava(scalaGroupAsJavaGroupConversion(b))
  }

  implicit def javaGroupAsScalaGroupConverter(b: jDirectory.model.Group): AsScala[sDirectory.models.Group] = {
    new AsScala(javaGroupAsScalaGroupConversion(b))
  }

  implicit def scalaMemberAsJavaMemberConverter(b: sDirectory.models.Member): AsJava[jDirectory.model.Member] = {
    new AsJava(scalaMemberAsJavaMemberConversion(b))
  }

  implicit def javaMemberAsScalaMemberConverter(b: jDirectory.model.Member): AsScala[sDirectory.models.Member] = {
    new AsScala(javaMemberAsScalaMemberConversion(b))
  }

  implicit def scalaUserAsJavaUserConverter(b: sDirectory.models.User): AsJava[jDirectory.model.User] = {
    new AsJava(scalaUserAsJavaUserConversion(b))
  }

  implicit def javaUserAsScalaUserConverter(b: jDirectory.model.User): AsScala[sDirectory.models.User] = {
    new AsScala(javaUserAsScalaUserConversion(b))
  }

  implicit def scalaNameAsJavaUserNameConverter(b: sDirectory.models.Name): AsJava[jDirectory.model.UserName] = {
    new AsJava(scalaNameAsJavaUserNameConversion(b))
  }

  implicit def javaUserNameAsScalaNameConverter(b: jDirectory.model.UserName): AsScala[sDirectory.models.Name] = {
    new AsScala(javaUserNameAsScalaNameConversion(b))
  }

  implicit def scalaEmailAsJavaUserEmailConverter(b: sDirectory.models.Email): AsJava[jDirectory.model.UserEmail] = {
    new AsJava(scalaEmailAsJavaUserEmailConversion(b))
  }

  implicit def javaUserEmailAsScalaEmailConverter(b: jDirectory.model.UserEmail): AsScala[sDirectory.models.Email] = {
    new AsScala(javaUserEmailAsScalaEmailConversion(b))
  }



}
