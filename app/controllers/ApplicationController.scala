package controllers

import akka.io.dns.internal.DnsClient.DnsQuestion
import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok {
        Json.toJson(item)
      }
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map {
      case data: DataModel => Ok(Json.toJson(data)) // Successfully found item
      case _ => NotFound(Json.toJson(s"Unable to find data for ID: $id")) // Handles missing data and errors
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => dataRepository.update(id, dataModel).flatMap {
        _ => dataRepository.read(id).map(book => Accepted {
          Json.toJson(book)
        })
      }
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => dataRepository.delete(id).map(_ => Accepted)
      case JsError(_) => Future(BadRequest)
    }
  }

}


