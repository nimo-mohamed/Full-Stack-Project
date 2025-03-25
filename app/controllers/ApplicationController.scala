package controllers

import com.mongodb.client.result.UpdateResult
import models.{APIError, DataModel, GoogleBook}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.{ApplicationService, RepositoryService}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val repositoryService: RepositoryService, val service: ApplicationService)(implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.index().map {
      case Right(item: Seq[DataModel]) => Ok(Json.toJson(item))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        Status(statusCode)(Json.toJson(message))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repositoryService.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    repositoryService.read(id).map {
      case Right(item: DataModel) => Ok(Json.toJson(item))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        Status(statusCode)(Json.toJson(message))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => // further validation of fields
        repositoryService.update(id, dataModel).map {
          case Right(result: UpdateResult) => Accepted(Json.toJson(dataModel))
          case Left(APIError.BadAPIResponse(statusCode, message)) =>
            Status(statusCode)(Json.toJson(message))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => repositoryService.delete(id).map(_ => Accepted)
      case JsError(_) => Future(NotFound)
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {

      case Right(book) => Ok(Json.toJson(book))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        NotFound(Json.obj(
          "error" -> s"Unable to find book: $term",
          "statusCode" -> statusCode,
          "details" -> message
        ))
    }
  }

  def findByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.findByName(name).map {
      case Right(item: DataModel) => Ok(Json.toJson(item))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        Status(statusCode)(Json.toJson(message))
    }
  }
}


