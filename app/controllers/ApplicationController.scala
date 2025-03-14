package controllers

import play.api.mvc._
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val repository: DataRepository, implicit val ec: ExecutionContext) extends BaseController {
  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.index()))
  }
  def create(): Action[AnyContent] = TODO
  def read(id: String): Action[AnyContent] = TODO
  def update(id: String): Action[AnyContent] = TODO
  def delete(id: String): Action[AnyContent] = TODO
}


