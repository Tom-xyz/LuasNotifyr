package controllers

import models._
import utils.silhouette._
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.{ MessagesApi, Messages, Lang }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import javax.inject.Inject

import scala.concurrent.Future

class Application @Inject() (val env: AuthenticationEnvironment, val messagesApi: MessagesApi) extends AuthenticationController {

  def index = UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.index()))
  }

  def myAccount = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.myAccount()))
  }

  // REQUIRED ROLES: serviceA (or master)
  def notifyr = SecuredAction(WithService("Notifyr")).async { implicit request =>
    Future.successful(Ok(views.html.notifyr()))
  }

}