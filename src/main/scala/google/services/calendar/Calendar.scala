package google.services.calendar

import google.services.Service

/**
  * Created by davenpcm on 5/5/16.
  */
case class Calendar(service: Service) {
  val calendar = new com.google.api.services.calendar.Calendar.Builder(service.httpTransport, service.jsonFactory, service.credential)
    .setApplicationName(service.applicationName)
    .setHttpRequestInitializer(service.credential)
    .build()

}
