package models

case class LuasAlarm(
                      user_id: Long,
                      station_short_name: String,
                      dayList: List[String],
                      alarmTime: String,
                      direction: String,
                      alarm_name: String) {


}
