package controllers

import play.api.mvc._

import javax.inject.Inject

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def index(): Action[AnyContent] = TODO
}


