package scripts

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.{Directory, DirectoryScopes}
import com.google.api.services.admin.directory.model.{User, Users}
import collection.JavaConverters._
import scala.annotation.tailrec
import persistence.entities.representations.GoogleIdentity

/**
  * Created by davenpcm on 4/19/2016.
  */
object GoogleAdmin{

  /**
    * This function is currently configured for a service account to interface with the school. It has been granted
    * explicitly this single grant so to change the scope these need to be implemented in Google first.
    *
    * @return A Google Directory Object which can be used to look through users with current permissions
    */
  private def getDirectoryService: Directory = {
    import java.util._

    val SERVICE_ACCOUNT_EMAIL = "wso2-admin2@ellucian-identity-service-1282.iam.gserviceaccount.com"
    val SERVICE_ACCOUNT_PKCS12_FILE_PATH = "C:/Users/davenpcm/Downloads/Ellucian Identity Service-8c565b08687e.p12"
    val APPLICATION_NAME = "Ellucian Identity Service"
    val USER_EMAIL = "wso_admin@eckerd.edu"

    val httpTransport = new NetHttpTransport()
    val jsonFactory = new JacksonFactory

    val credential = new GoogleCredential.Builder()
      .setTransport( httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
      .setServiceAccountScopes( Collections.singleton( DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY ) )
      .setServiceAccountPrivateKeyFromP12File(
        new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH)
      )
      .setServiceAccountUser(USER_EMAIL)
      .build()

    if (!credential.refreshToken()) {
      throw new RuntimeException("Failed OAuth to refresh the token")
    }

    val directory = new Directory.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(APPLICATION_NAME)
      .setHttpRequestInitializer(credential)
      .build()

    directory
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
  private def listAllUsers(service: Directory, pageToken: String = "", users: List[Users] = List[Users]()): List[Users] = {

    val result = service.users()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setOrderBy("email")
      .setPageToken(pageToken)
      .execute()

    val list = result :: users


    val nextPageToken = result.getNextPageToken
    if (nextPageToken != null && result.getUsers != null) listAllUsers(service, nextPageToken, list) else list

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
  private def transformAllGoogleUsers[T](service: Directory,
                                    pageToken: String = "",
                                    transformed: List[T] = List[T]()
                                   )(f: User => T): List[T] = {

    val result = service.users()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setOrderBy("email")
      .setPageToken(pageToken)
      .execute()

    val typedList = List[Users](result)
      .map(users => users.getUsers.asScala.toList)
      .foldLeft(List[User]())((acc, listUsers) => listUsers ::: acc)
      .map(user => f(user))

    val list: List[T] = typedList ::: transformed

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

    val service = getDirectoryService
    val googleIdentitiesSets = transformAllGoogleUsers[GoogleIdentity](service)(UserToGoogleIdent)

    googleIdentitiesSets
  }

}
