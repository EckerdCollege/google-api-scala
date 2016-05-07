package google.services.drive

import google.services.Service
import language.implicitConversions

/**
  * Created by davenpcm on 5/5/16.
  */
case class Drive(service: Service) {
  val drive = new com.google.api.services.drive.Drive.Builder(service.httpTransport, service.jsonFactory, service.credential)
    .setApplicationName(service.applicationName)
    .setHttpRequestInitializer(service.credential)
    .build()

  val files = new files(this)
  val permissions = new permissions(this)

}
object Drive {
  implicit def toGoogleApi(drive: Drive): com.google.api.services.drive.Drive = {
    drive.drive
  }
}
