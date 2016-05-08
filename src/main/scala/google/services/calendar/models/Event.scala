package google.services.calendar.models
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
/**
  * Created by davenpcm on 5/8/16.
  */
case class Event(title: String,
                 description: Option[String],
                 startTime: Option[ZonedDateTime],
                 endTime: Option[ZonedDateTime],
                 participantEmails: Option[List[String]] = None,
                 recurrence: Option[List[String]] = None ,
                 id: Option[String] = None
                ) {
}

