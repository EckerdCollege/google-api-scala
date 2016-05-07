package google.services.admin.directory

import google.services.admin.directory.models.{User, Email, Name}
import scala.language.implicitConversions
import scala.language.postfixOps
import scala.annotation.tailrec
import scala.util.Try
import scala.util.Success
import scala.util.Failure

/**
  * Created by davenpcm on 5/3/16.
  */
class users(directory: Directory) {

  private val service: com.google.api.services.admin.directory.Directory = directory

  private implicit def userToGoogleApi(user: User): com.google.api.services.admin.directory.model.User = {
    val newUser = new com.google.api.services.admin.directory.model.User
    newUser
      .setChangePasswordAtNextLogin(user.changePasswordAtNextLogin)
      .setIncludeInGlobalAddressList(user.includeInGlobalAddressList)
      .setIpWhitelisted(user.ipWhiteListed)
      .setIsAdmin(user.isAdmin)
      .setIsMailboxSetup(user.isMailboxSetup)
      .setSuspended(user.suspended)
      .setOrgUnitPath(user.orgUnitPath)
      .setPrimaryEmail(user.primaryEmail)
      .setName(user.name)

    if (user.agreedToTerms isDefined) { newUser.setAgreedToTerms( user.agreedToTerms.get)}
    if (user.password isDefined) { newUser.setPassword( user.password.get)}


    newUser
  }

  private implicit def userFromGoogleApi(user: com.google.api.services.admin.directory.model.User): User = {
    User(
      user.getName,
      Email(user.getPrimaryEmail),
      Option(user.getPassword),
      Option(user.getId),
      user.getOrgUnitPath,
      Option(user.getAgreedToTerms) match {
        case Some(value ) => Some(value)
        case None => None
      },
      user.getChangePasswordAtNextLogin,
      user.getIncludeInGlobalAddressList,
      user.getIpWhitelisted,
      user.getIsAdmin,
      user.getIsMailboxSetup,
      user.getSuspended
    )
  }

  private implicit def nameToGoogleApi(name: Name): com.google.api.services.admin.directory.model.UserName = {
    new com.google.api.services.admin.directory.model.UserName()
      .setGivenName(name.givenName)
      .setFamilyName(name.familyName)
  }

  private implicit def nameFromGoogleApi(userName: com.google.api.services.admin.directory.model.UserName): Name = {
    Name(
      userName.getGivenName,
      userName.getFamilyName
    )
  }

  private implicit def emailToGoogleApi(email: Email): com.google.api.services.admin.directory.model.UserEmail = {
    new com.google.api.services.admin.directory.model.UserEmail()
      .setAddress(email.address)
      .setPrimary(email.primary)
  }

  private implicit def emailFromGoogleApi(userEmail: com.google.api.services.admin.directory.model.UserEmail): Email = {
    Email(
      userEmail.getAddress,
      userEmail.getPrimary
    )
  }

  /**
    * This is a simple user accumulation function that collects all users in eckerd. It does no additional modification
    * so that you can easily move from users to whatever other form you would like and do those pieces of data
    * manipulation after all the users have been returned.
    *
    * @return
    */
  def list(domain: String = "eckerd.edu", orderBy: String = "email", resultsPerPage: Int = 500): List[User] = {
    @tailrec
    def list(pageToken: String = "", users: List[User] = List[User]()): List[User] = {
      import com.google.api.services.admin.directory.model.Users
      import collection.JavaConverters._

      val result = service.users()
        .list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setOrderBy(orderBy)
        .setPageToken(pageToken)
        .execute()

      val typedList = List[Users](result)
        .map(users => users.getUsers)
        .map { javaList => javaList.asScala.toList }
        .foldLeft(List[User]())((acc, listUsers) => listUsers.map(userFromGoogleApi) ::: acc)

      val myList = typedList ::: users

      val nextPageToken = result.getNextPageToken
      if (nextPageToken != null && result.getUsers != null) list(nextPageToken, myList) else myList

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
      import com.google.api.services.admin.directory.model.Users
      import collection.JavaConverters._

      val result = service.users()
        .list()
        .setDomain(domain)
        .setMaxResults(resultsPerPage)
        .setOrderBy(orderBy)
        .setPageToken(pageToken)
        .execute()

      lazy val typedList = List[Users](result)
        .map(users => users.getUsers)
        .map { javaList => javaList.asScala.toList }
        .foldLeft(List[User]())((acc, listUsers) => listUsers.map(userFromGoogleApi) ::: acc)
        .map(user => f(user))

      lazy val list: List[T] = typedList ::: transformed

      val nextPageToken = result.getNextPageToken

      if (nextPageToken != null && result.getUsers != null) transformAllGoogleUsers(nextPageToken, list) else list
    }
    transformAllGoogleUsers()
  }

  def create(user: User): User = {
    service.users().insert(user).execute()
  }

  def create(name: Name, emailAddress: String, password: String): User = {
    val email = Email(emailAddress)
    val user = User(name, email, Some(password))
    service.users().insert(user).execute()
  }

  def create(givenName: String, familyName: String, email: Email, password: String): User = {
    val name = Name(givenName, familyName)
    val user = User(name, email, Some(password))
    service.users().insert(user).execute()
  }

  def create(givenName: String, familyName: String, emailAddress: String, password: String): User = {
    val name = Name(givenName, familyName)
    val email = Email(emailAddress)
    val user = User(name, email, Some(password))
    service.users().insert(user).execute()
  }

  def update(user: User): User = {
    service.users().update(user.id.get, user).execute()
  }


}
