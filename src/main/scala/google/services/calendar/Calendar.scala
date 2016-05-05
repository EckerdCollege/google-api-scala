package google.services.calendar

import google.services.Service
import language.implicitConversions

/**
  * Created by davenpcm on 5/5/16.
  */
case class Calendar(service: Service) {
  val calendar = new com.google.api.services.calendar.Calendar.Builder(service.httpTransport, service.jsonFactory, service.credential)
    .setApplicationName(service.applicationName)
    .setHttpRequestInitializer(service.credential)
    .build()
}

object Calendar {
  implicit def toGoogleApi(calendar: Calendar): com.google.api.services.calendar.Calendar = {
    calendar.calendar
  }
}
