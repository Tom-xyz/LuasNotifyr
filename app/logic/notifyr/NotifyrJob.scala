import akka.actor.Props
import logic.notifyr.{LuasJobActor, LuasRTPI}
import play.api.{Application, GlobalSettings}
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.duration.DurationInt


object Global extends GlobalSettings {


  override def onStart(app: Application) {
    //Define LuasRTPI Object
    LuasRTPI
    alarmCheck(app)
  }

  def alarmCheck(app: Application): Unit = {
    val luasJobActor = Akka.system(app).actorOf(Props(new LuasJobActor()))
    println("Scheduling job for every minute")
    Akka.system(app).scheduler.schedule(0 seconds, 5 minutes, luasJobActor, "startAlarmCheck")
  }

}
