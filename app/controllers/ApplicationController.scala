package controllers

import play.api.mvc._

import javax.inject._

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def index(): Action[AnyContent] = TODO
  def create(): Action[AnyContent] = TODO
  def read(id: String): Action[AnyContent] = TODO
  def update(id: String): Action[AnyContent] = TODO
  def delete(id: String): Action[AnyContent] = TODO
}


