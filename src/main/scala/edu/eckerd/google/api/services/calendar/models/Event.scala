package edu.eckerd.google.api.services.calendar.models
import java.time.ZonedDateTime

case class Event(title: String,
                 description: Option[String],
                 startTime: Option[ZonedDateTime],
                 endTime: Option[ZonedDateTime],
                 participantEmails: Option[List[String]] = None,
                 recurrence: Option[List[String]] = None ,
                 id: Option[String] = None
                ) {
}

