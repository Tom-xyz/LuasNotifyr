package controllers


import javax.inject.Inject

import logic.notifyr.LuasRTPI
import models.LuasAlarm
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.MessagesApi
import play.api.mvc._
import utils.silhouette.{AuthenticationController, AuthenticationEnvironment, WithService}
import views._

import scala.concurrent.Future

class Notifyr @Inject()(val env: AuthenticationEnvironment, val messagesApi: MessagesApi) extends AuthenticationController {

  val luasForm: Form[LuasAlarm] = Form(
    mapping(
      "user_id" -> longNumber,
      "Station" -> text,
      "days" -> Forms.list(nonEmptyText),
      "Time" -> text,
      "Direction" -> text,
      "Alarm Name" -> text
    )(LuasAlarm.apply)(LuasAlarm.unapply)
  )


  def notifyr = SecuredAction(WithService("Notifyr")).async { implicit request =>
    Future.successful(Ok(views.html.notifyr(luasForm)))
  }

  def addLuasNotifyr = Action {
    Ok("Great")
  }

  /**
    * Display an empty form.
    */
  def form = SecuredAction(WithService("Notifyr")).async { implicit request =>
    Future.successful(Ok(html.notifyr(luasForm)));
  }


  /**
    * Handle form submission.
    */
  def submit = SecuredAction(WithService("Notifyr")).async { implicit request =>
    luasForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => {
        println(errors)
        Future.successful(BadRequest(html.notifyr(errors)))
      },

      luasAlarm => {
        val luasAlarmList = LuasRTPI.LUAS_ALARMS.get(luasAlarm.user_id)
        if (!luasAlarmList.isDefined) {
          LuasRTPI.LUAS_ALARMS += ((luasAlarm.user_id, List(luasAlarm)))
        } else {
          LuasRTPI.LUAS_ALARMS += ((luasAlarm.user_id, luasAlarmList.get :+ luasAlarm))
        }


        println("TEST " + LuasRTPI.LUAS_ALARMS)
        Future.successful(Ok(html.notifyr(luasForm)))
      }
    )
  }


}
