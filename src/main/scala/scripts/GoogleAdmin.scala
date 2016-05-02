package scripts

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.admin.directory
import com.google.api.services.admin.directory.{Directory, DirectoryScopes}
import com.google.api.services.admin.directory.model._
import com.typesafe.config.ConfigFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model._
import com.google.api.services.drive.DriveScopes
import com.google.api.services.gmail.GmailScopes

import collection.JavaConverters._
import scala.annotation.tailrec
import persistence.entities.representations.GoogleIdentity

import scala.util.{Failure, Success, Try}


/**
  * Created by davenpcm on 4/19/2016.
  */
object GoogleAdmin{

  val ListScopes = List(
    DirectoryScopes.ADMIN_DIRECTORY_USER,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP,
    DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER,
    CalendarScopes.CALENDAR,
    DriveScopes.DRIVE,
    GmailScopes.GMAIL_COMPOSE
  )
  val Scopes = ListScopes.foldRight("")((a,b) => a + "," + b).dropRight(1)
  /**
    * This function is currently configured for a service account to interface with the school. It has been granted
    * explicitly these grants so to change the scope these need to be implemented in Google first.
    *
    * @param DirectoryScope This is the scope that is requested for whatever operations you would like to perform.
    * @return A Google Directory Object which can be used to look through Admin Directory Information
    */
  private def getDirectoryService(DirectoryScope:String): Directory = {
    import java.util._

    val conf = ConfigFactory.load().getConfig("google")
    val SERVICE_ACCOUNT_EMAIL = conf.getString("email")
    val SERVICE_ACCOUNT_PKCS12_FILE_PATH = conf.getString("pkcs12FilePath")
    val APPLICATION_NAME = conf.getString("applicationName")
    val USER_EMAIL = conf.getString("impersonatedEmail")


    val httpTransport = new NetHttpTransport()
    val jsonFactory = new JacksonFactory

    val credential = new GoogleCredential.Builder()
      .setTransport( httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
      .setServiceAccountScopes( Collections.singleton( DirectoryScope ) )
      .setServiceAccountPrivateKeyFromP12File(
        new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)
      )
      .setServiceAccountUser(USER_EMAIL)
      .build()

//    if (!credential.refreshToken()) {
//      throw new RuntimeException("Failed OAuth to refresh the token")
//    }

    val directory = new Directory.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(APPLICATION_NAME)
      .setHttpRequestInitializer(credential)
      .build()

    directory
  }

  private def getCalendarService(Scope:String, USER_EMAIL: String): com.google.api.services.calendar.Calendar
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

    //    if (!credential.refreshToken()) {
    //      throw new RuntimeException("Failed OAuth to refresh the token")
    //    }

    val calendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(APPLICATION_NAME)
      .setHttpRequestInitializer(credential)
      .build()

    calendar
  }

  /**
    * This is a simple user accumulation function that collects all users in eckerd. It does no additional modification
    * so that you can easily move from users to whatever other form you would like and do those pieces of data
    * manipulation after all the users have been returned.
    *
    * @param service A directory to check
    * @param pageToken A page token which indicates where you are in the sequence of pages of users
    * @param users A List Used For Recursive Accumulation through the function. We build a large list of users
    * @return
    */
  @tailrec
  def listAllUsers(service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_USER),
                   pageToken: String = "",
                   users: List[User] = List[User]()): List[User] = {

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

    val list = typedList ::: users


    val nextPageToken = result.getNextPageToken
    if (nextPageToken != null && result.getUsers != null) listAllUsers(service, nextPageToken, list) else list

  }

  /**
    *  This returns the user from a string. Be Aware this user
    * @param identifier The identifier used to get the user
    * @param service The service to use to to find the user
    * @return
    */
  def getUser(identifier: String,
              service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_USER)): Option[User] = {
    val user = service.users().get(identifier).execute()
    val exists = Option(user.getId)
    exists match {
      case None => None
      case Some(value) => Some(user)
    }
  }

  /**
    * This function simply returns a list of all groups in the organization.
    *
    * @param service The Directory Service We Are Getting the Groups From
    * @param pageToken The Page Token for the current page. We Initialize As An Empty String and automatically fill it
    *                  out as we move through the pages
    * @param groups This is the growing list of groups that is recursively passed through the function
    * @return A list of all groups.
    */
  @tailrec
  def listAllGroups(service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP),
                    pageToken: String = "",
                    groups: List[Group] = List[Group]()
                   ): List[Group] ={

    val result = service.groups()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setPageToken(pageToken)
      .execute()

    val typedList = List[Groups](result)
      .map(groups => groups.getGroups.asScala.toList)
      .foldLeft(List[Group]())((acc, listGroups) => listGroups ::: acc)

    val list = typedList ::: groups

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getGroups != null) listAllGroups(service, nextPageToken, list) else list

  }

  /**
    * The function takes a groupKey to return all members of the group. Current Error Handling will terminate on an error
    * from our side, but will retry if it is a bad return response from google, likely a service unavailable. Which
    * occurs sporadically. 
    *
    * @param groupKey This is either the unique group id or the group email address. Which specifies which group to
    *                 return the members of
    * @param service This is the service we will be utilizing to get the members. It is initialized as a Parameter as
    *                that way it is only initialized on entry to the loop and then the same directory is used each time
    * @param pageToken This is the page token that shows where we are in the pages.
    * @param members This is the growing list of group members
    * @return A list of all members of the group
    */
  @tailrec
  def listAllGroupMembers(groupKey: String,
                          service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP),
                          pageToken: String = "",
                          members: List[Member] = List[Member]()
                          ): List[Member] = {

    val resultTry = Try(
      service.members()
      .list(groupKey)
      .setMaxResults(500)
      .setPageToken(pageToken)
      .execute()
    )

    resultTry match {
      case Success(result) =>
        val typedList = List[Members](result)
          .map(members => Option(members.getMembers))
          .map{ case Some(member) => member.asScala.toList case None => List[Member]() }
          .foldLeft(List[Member]())((acc, listMembers) => listMembers ::: acc)

        val list = typedList ::: members

        val nextPageToken = result.getNextPageToken

        if (nextPageToken != null && result.getMembers != null) listAllGroupMembers(groupKey, service, nextPageToken, list) else list

      case Failure(exception: GoogleJsonResponseException) => listAllGroupMembers(groupKey, service, pageToken, members)
      case Failure(e) => throw e
    }

  }

  /**
    * This function is a Type free implementation that allows you to tell you what type you are expecting as a return
    * and it will fully type check that you are getting the type you want back.
    *
    * @param service This is the directory to be returned
    * @param pageToken This is a page token to show where in the series of pages you are.
    * @param transformed A List of Type T, is Initialized to an Empty List that is expanded recursively through each
    *                    of the pages
    * @param f This is a transformation function that takes a User and Transforms it to Type T
    * @tparam T This is any type that you want to return from the google users.
    * @return Returns a List of Type T
    */
  @tailrec
  private def transformAllGoogleUsers[T](service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_USER),
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

    if (nextPageToken != null && result.getUsers != null) transformAllGoogleUsers(service, nextPageToken, list)(f) else list
  }



  /**
    * Returns a List of Google Identities for Eckerd College
    * @return List of GoogleIdentity
    */
  def ReturnAllGoogleIdentities(): List[GoogleIdentity] = {

    /**
      * Simple Transformation between the Google User Objects ID and Primary Email and a GoogleIdentity Case Class
      * @param user Google User Object
      * @return A GoogleIdentity
      */
    def UserToGoogleIdent(user: User): GoogleIdentity = {
      GoogleIdentity(user.getId, user.getPrimaryEmail)
    }

    val googleIdentitiesSets = transformAllGoogleUsers[GoogleIdentity]()(UserToGoogleIdent)

    googleIdentitiesSets
  }

  def CreateGroup(group: Group,
                  service: Directory= getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP)):Group = {
    service.groups().insert(group).execute()
  }

  def DeleteGroup(groupKey: String,
                  service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
                 ): Unit = {
    service.groups().delete(groupKey).execute()
  }

  def AddUserToGroup(groupKey: String, id: String,
                     service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
                    ): Member = {

    val newMember = new Member
    newMember.setId(id)
    newMember.setRole("MEMBER")

    val InsertedMember = service.members().insert(groupKey, newMember).execute()
    InsertedMember
  }

  def RemoveUserFromGroup(groupKey: String,
                          userId: String,
                          service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_GROUP)
                         ): Unit = {
    service.members().delete(groupKey, userId).execute()
  }

  def GetUserPhoto(userKey: String,
                   service: Directory = getDirectoryService(DirectoryScopes.ADMIN_DIRECTORY_USER)
                  ): Option[UserPhoto] = {

    val returnType = Try( service.users().photos().get(userKey).execute() )

    returnType match {
      case Success(value) => Some(value)
      case Failure(exception) => None
    }
  }

  def GetCalendarEvents(userEmail: String): List[Event] = {
    val now = new DateTime(System.currentTimeMillis())
    val service = getCalendarService(CalendarScopes.CALENDAR, userEmail)
    val events = service.events.list("primary")
      .setMaxResults(500)
      .setTimeMin(now)
      .setOrderBy("startTime")
      .setSingleEvents(true)
      .execute()
      .getItems

    events.asScala.toList
  }

  private def PutCalendarEvent(userEmail: String, event: Event): Unit = {
    val service = getCalendarService(CalendarScopes.CALENDAR, userEmail)
    service.events()
      .insert("primary", event)
      .setSendNotifications(true)
      .execute()
  }

  def CreateEvent(title: String,
                  description: String,
                  startTime: String,
                  endTime: String,
                  primaryEmail: String,
                  participantEmails: List[String] = List[String](),
                  recurrence: String = ""
                 ): Unit = {
    val event = new Event
    val start = new EventDateTime()
      .setDateTime(new DateTime(startTime))
      .setTimeZone("America/New_York")
    val end = new EventDateTime()
      .setDateTime(new DateTime(endTime))
      .setTimeZone("America/New_York")
    val participants = participantEmails.map( participantEmail =>
      new EventAttendee().setEmail(participantEmail)
    ).asJava


    event.setSummary(title)
    event.setDescription(description)
    event.setStart(start)
    event.setEnd(end)
    event.setAttendees(participants)
    if (recurrence != ""){
      val recurrenceList = List(recurrence).asJava
      event.setRecurrence(recurrenceList)
    }

    PutCalendarEvent(primaryEmail, event)
  }


}
