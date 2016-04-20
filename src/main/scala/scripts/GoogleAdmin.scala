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

  @tailrec
  private def pageAllGoogleUsers[T](service: Directory, pageToken: String = "", transformed: List[T] = List[T]())(f: Users => T):
  scala.List[T] = {

    val result = service.users()
      .list()
      .setDomain("eckerd.edu")
      .setMaxResults(500)
      .setOrderBy("email")
      .setPageToken(pageToken)
      .execute()

    val list: scala.List[T] = f(result) :: transformed

    val nextPageToken = result.getNextPageToken

    if (nextPageToken != null && result.getUsers != null) pageAllGoogleUsers(service, nextPageToken, list)(f) else list
  }


  private def UsersToGoogleIdents(users: Users): scala.List[GoogleIdentity] = {
    val list : List[Users] = List[Users](users)

    val googleIdentList = list.map(users => users.getUsers.asScala.toList)
      .foldLeft(List[User]())((acc, listUsers) => listUsers ::: acc)
      .map(user => GoogleIdentity(user.getId, user.getPrimaryEmail))

    googleIdentList
  }

  def ReturnAllGoogleIdentities(): scala.List[GoogleIdentity] = {

    val service = getDirectoryService
    val googleIdentitiesSets = pageAllGoogleUsers[List[GoogleIdentity]](service)(UsersToGoogleIdents)
    val googleIdentities = googleIdentitiesSets.foldLeft(List[GoogleIdentity]())((acc, listSeqs) => listSeqs ::: acc)
    googleIdentities
  }

}
