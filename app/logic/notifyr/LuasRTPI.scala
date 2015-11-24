package logic.notifyr


import java.util.concurrent.TimeUnit

import com.ning.http.client.{ListenableFuture, AsyncHttpClient}
import dispatch._
import models.LuasAlarm

import play.api.libs.json.{JsValue, Json}


object LuasRTPI {

  val LUAS_STATIONS_REST_URL = "http://luas.neilcremins.com/?action=stations"
  val LUAS_STATION_INFO_REST_URL = "http://luas.neilcremins.com/?action=times&station="
  var LUAS_STATION_MAP = Map[String, String]()
  var LUAS_DIRECTION = List("Point", "Conolly", "Tallaght", "Sandyford", "Brides Glen", "St. Stephen's Green", "Saggart")
  var LUAS_ALARMS = Map[Long, List[LuasAlarm]]()
  val ALARM_DAYS = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

  val asyncHttpClient = new AsyncHttpClient()
  val response : ListenableFuture[Res] = asyncHttpClient.prepareGet(LUAS_STATIONS_REST_URL).execute()

  val json_response: JsValue = Json.parse(response.get().getResponseBody)

  val display_name = (json_response \\ "displayName").map(_.as[String])
  val short_name = (json_response \\ "shortName").map(_.as[String])

  LUAS_STATION_MAP = (short_name zip display_name).toMap

  def getStationInfo(station : String) : Map[String, String] = {
    val response : ListenableFuture[Res] = asyncHttpClient.prepareGet(LUAS_STATION_INFO_REST_URL+station).execute()
    val json_response: JsValue = Json.parse(response.get().getResponseBody)

    println(json_response)

    val destination = (json_response \\ "destination").map(_.as[String])
    val due_minutes = ((json_response \\ "dueMinutes")).map(_.as[String])

    (destination zip due_minutes).toMap.slice(0,1)
  }

}
