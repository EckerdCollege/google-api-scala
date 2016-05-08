package google.services.calendar.models
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
/**
  * Created by davenpcm on 5/8/16.
  */
case class Event(title: String,
            description: String,
            startTime: Option[ZonedDateTime],
            endTime: Option[ZonedDateTime],
            participantEmails: List[String] = List[String](),
            recurrence: List[String] = List[String]()
           ) {
}

