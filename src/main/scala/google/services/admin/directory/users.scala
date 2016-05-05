package google.services.admin.directory

import com.google.api.services.admin.directory.model.{User, Users}

import scala.annotation.tailrec
import collection.JavaConverters._
import scala.util.Try
import scala.util.Success
import scala.util.Failure

/**
  * Created by davenpcm on 5/3/16.
  */
class users(directory: Directory) {

  val service = directory.directory

  /**
    * This is a simple user accumulation function that collects all users in eckerd. It does no additional modification
    * so that you can easily move from users to whatever other form you would like and do those pieces of data
    * manipulation after all the users have been returned.
    *
    * @param pageToken A page token which indicates where you are in the sequence of pages of users
    * @param users A List Used For Recursive Accumulation through the function. We build a large list of users
    * @return
    */
  @tailrec
  final def list(pageToken: String = "", users: List[User] = List[User]()): List[User] = {

    val result = service.users()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setOrderBy("email")
      .setPageToken(pageToken)
      .execute()

    val typedList = List[Users](result)
      .map(users => Option(users.getUsers))
      .map{ case Some(javaList) => javaList.asScala.toList case None => List[User]()}
      .foldLeft(List[User]())((acc, listUsers) => listUsers ::: acc)

    val myList = typedList ::: users

    val nextPageToken = result.getNextPageToken
    if (nextPageToken != null && result.getUsers != null) list(nextPageToken, myList) else myList

  }

  /**
    *  This returns the user from a string. Be Aware this user
    * @param identifier The identifier used to get the user
    * @return
    */
  def get(identifier: String): Either[Throwable, User] = {
    val attempt = Try(service.users().get(identifier).execute())
    attempt match {
      case Success(member)=> Right(member)
      case Failure(e) => Left(e)
    }

  }

  /**
    * This function is a Type free implementation that allows you to tell you what type you are expecting as a return
    * and it will fully type check that you are getting the type you want back.
    *
    * @param pageToken This is a page token to show where in the series of pages you are.
    * @param transformed A List of Type T, is Initialized to an Empty List that is expanded recursively through each
    *                    of the pages
    * @param f This is a transformation function that takes a User and Transforms it to Type T
    * @tparam T This is any type that you want to return from the google users.
    * @return Returns a List of Type T
    */
  @tailrec
  final def transformAllGoogleUsers[T](
                                         pageToken: String = "",
                                         transformed: => List[T] = List[T]()
                                        )(f: User => T): List[T] = {

    val result = service.users()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setOrderBy("email")
      .setPageToken(pageToken)
      .execute()

    lazy val typedList = List[Users](result)
      .map(users => Option(users.getUsers))
      .map{ case Some(javaList) => javaList.asScala.toList case None => List[User]()}
      .foldLeft(List[User]())((acc, listUsers) => listUsers ::: acc)
      .map(user => f(user))

    lazy val list: List[T] = typedList ::: transformed

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getUsers != null) transformAllGoogleUsers(nextPageToken, list)(f) else list
  }


}
