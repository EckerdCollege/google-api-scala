package edu.eckerd.google.api.services.directory

import edu.eckerd.google.api.language.JavaConverters._
import edu.eckerd.google.api.services.directory.models.{Email, Name, User}

import scala.language.{implicitConversions, postfixOps}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

class users(directory: Directory) {

  private val service = directory.asJava

  val photos = new photos(directory)

  /**
    * This is a simple user accumulation function that collects all users in eckerd. It does no additional modification
    * so that you can easily move from users to whatever other form you would like and do those pieces of data
    * manipulation after all the users have been returned.
    *
    * @return
    */
  def list(domain: String = "eckerd.edu"): List[User] = {
    @tailrec
    def list(pageToken: String = "",
             users: List[User] = List[User](),
             orderBy: String = "email",
             resultsPerPage: Int = 500): List[User] = {

      val result = service.users()
        .list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setOrderBy(orderBy)
        .setPageToken(pageToken)
        .execute().asScala

      val myList = result.users.getOrElse(List[User]()) ::: users

      if (result.nextPageToken.isDefined && result.users.isDefined) list(result.nextPageToken.get, myList)
      else myList

    }
    list()
  }

  /**
    *  This returns the user from a string. Be Aware this user
    *
    * @param identifier The identifier used to get the user
    * @return
    */
  def get(identifier: String): Either[Throwable, User] = {
    val attempt = Try(service.users().get(identifier).execute().asScala)
    attempt match {
      case Success(member)=> Right(member)
      case Failure(e) => Left(e)
    }

  }

  /**
    * This function is a Type free implementation that allows you to tell you what type you are expecting as a return
    * and it will fully type check that you are getting the type you want back.
    *
    * @param f This is a transformation function that takes a User and Transforms it to Type T
    * @tparam T This is any type that you want to return from the google users.
    * @return Returns a List of Type T
    */
  def transformAllGoogleUsers[T](domain: String = "eckerd.edu",
                                 orderBy: String = "email",
                                 resultsPerPage: Int = 500)(f: User=> T): List[T] = {
    @tailrec
    def transformAllGoogleUsers(
                                 pageToken: String = "",
                                 transformed: => List[T] = List[T]()
                                  ): List[T] = {

      val result = service.users().list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setOrderBy(orderBy)
        .setPageToken(pageToken)
        .execute().asScala


      lazy val list: List[T] = result.users.getOrElse(List[User]()).map(f) ::: transformed

      if (result.nextPageToken.isDefined && result.users.isDefined){
        transformAllGoogleUsers(result.nextPageToken.get, list)
      } else list
    }
    transformAllGoogleUsers()
  }

  def create(user: User): User = {
    service.users().insert(user.asJava).execute().asScala
  }

  def create(name: Name, emailAddress: String, password: String): User = {
    val email = Email(emailAddress)
    val user = User(name, email, Some(password))
    service.users().insert(user.asJava).execute().asScala
  }

  def create(givenName: String, familyName: String, email: Email, password: String): User = {
    val name = Name(givenName, familyName)
    val user = User(name, email, Some(password))
    service.users().insert(user.asJava).execute().asScala
  }

  def create(givenName: String, familyName: String, emailAddress: String, password: String): User = {
    val name = Name(givenName, familyName)
    val email = Email(emailAddress)
    val user = User(name, email, Some(password))
    service.users().insert(user.asJava).execute().asScala
  }

  def update(user: User): User = {
    service.users().update(user.id.get, user.asJava).execute().asScala
  }

}
