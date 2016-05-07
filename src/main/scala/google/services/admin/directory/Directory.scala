package google.services.admin.directory
import google.services.Service
import scala.language.implicitConversions
/**
  * Created by davenpcm on 5/5/16.
  */
case class Directory(service: Service) {

  private val directory = new com.google.api.services.admin.directory.Directory.Builder(
    service.httpTransport,
    service.jsonFactory,
    service.credential)
    .setApplicationName(service.applicationName)
    .setHttpRequestInitializer(service.credential)
    .build()

  val groups = new groups(this)
  val members = new members(this)
  val photos = new photos(this)
  val users = new users(this)
}

object Directory {
  implicit def toGoogleApi(Directory: Directory): com.google.api.services.admin.directory.Directory = {
    Directory.directory
  }
}
