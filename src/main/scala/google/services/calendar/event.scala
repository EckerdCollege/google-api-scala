package google.services.calendar

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.{Calendar, CalendarScopes}
import com.google.api.services.calendar.model.{Event, EventAttendee, EventDateTime}
import google.services.service._

import scala.collection.JavaConverters._

/**
  * Created by davenpcm on 5/3/16.
  */
class event {

  def list(service: Calendar): List[Event] = {
    val now = new DateTime(System.currentTimeMillis())
    val events = service.events.list("primary")
      .setMaxResults(500)
      .setTimeMin(now)
      .setOrderBy("startTime")
      .setSingleEvents(true)
      .execute()
      .getItems

    events.asScala.toList
  }

  def put(service: Calendar, event: Event): Event = {
    service.events()
      .insert("primary", event)
      .setSendNotifications(true)
      .execute()
  }

  def create(title: String,
                  description: String,
                  startTime: String,
                  endTime: String,
                  primaryEmail: String,
                  participantEmails: List[String] = List[String](),
                  recurrence: String = ""
                 ): Event = {
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

    event
  }
}
