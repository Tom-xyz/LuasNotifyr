package logic.notifyr

import java.text.SimpleDateFormat

import akka.actor.{Actor, Props}
import com.ning.http.client.AsyncHttpClient
import models.{LuasAlarm, User}
import org.joda.time.{DateTime, LocalDate}


object LuasJobActor {
  def props = Props[LuasJobActor]
}

class LuasJobActor extends Actor {

  val SMS_REST_ENDPOINT = "http://api.txtlocal.com/send/?"
  val SMS_API_KEY = "M6++DC2ovOM-ISG06Ls8mDNasH3XKGnCNA3muwDFbt"
  val FIVE_MILLI_MINUTE = 60000L
  val asyncHttpClient = new AsyncHttpClient()

  def receive = {
    case _ => {
      println("Starting Alarm Check")

      val alarmList = LuasRTPI.LUAS_ALARMS.flatMap(alarm => {
        alarm._2
      })

      val checkAlarm = alarmList.filter(alarm => checkTime(alarm))

      checkAlarm.foreach(alarm => {
        LuasRTPI.getStationInfo(alarm.station_short_name).foreach(tram => {
          val smsMessage = "Luas Notifyr Alarm Notification:" + alarm.direction + " Tram at " + LuasRTPI.LUAS_STATION_MAP.get(alarm.station_short_name).get + " station due in " + tram._2 + " minutes"
          val response = asyncHttpClient.prepareGet(SMS_REST_ENDPOINT + "apiKey=" + SMS_API_KEY + "&message=" + smsMessage + "&sender=Notifyr&numbers=" + User.users.get(alarm.user_id).get.mobile).execute();
          println(response.get().getResponseBody)
        })

      })

    }
  }

  def checkTime(alarm: LuasAlarm): Boolean = {
    val df = new SimpleDateFormat("HH:mm");
    val num_day = new DateTime()
    val alarmTime = df.parse(alarm.alarmTime)
    val alarmTime_day = num_day.withTime(alarmTime.getHours, alarmTime.getMinutes, 0, 0)
    val difference = alarmTime_day.toDate.getTime - num_day.toDate.getTime
    val dt: LocalDate = new LocalDate();
    var alarmTriggered = false

    alarm.dayList.foreach(a => {
      if (a == dt.dayOfWeek().getAsText) {
        if (difference <= FIVE_MILLI_MINUTE && difference >= 0L) {
          alarmTriggered = true
        }
      }
    })

    alarmTriggered
  }


}
