package controllers

import com.mongodb.client.result.UpdateResult
import models.GoogleBook.{IndustryIdentifier, VolumeInfo}
import models.{APIError, DataModel, GoogleBook}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.{ApplicationService, RepositoryService}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val repositoryService: RepositoryService, val service: ApplicationService)(implicit val ec: ExecutionContext) extends BaseController {


  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.flatMap {
      case Right(bookList) =>
        println(s"FIREBALL = ${bookList.items}")
        bookList.items.head match {
          case book => repositoryService.create(DataModel(book.volumeInfo.industryIdentifiers.head.identifier, book.volumeInfo.title.getOrElse("dummy title"), book.volumeInfo.description.getOrElse("dummy description"), book.volumeInfo.pageCount.getOrElse(0))).map(_ => Ok(views.html.index(book)))
          case _ =>
            Future(NotFound(Json.obj(
              "error" -> s"Unable to find book: $term",
              "statusCode" -> 404,
              "details" -> "didn't find the book!"
            )))
        }
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        Future(NotFound(Json.obj(
          "error" -> s"Unable to find book: $term",
          "statusCode" -> statusCode,
          "details" -> message
        )))
    }
  }

  private val isbn: String = "0134315057"

  //  def index(): Action[AnyContent] = Action.async { implicit request =>
  //    repositoryService.index().map {
  //      case Right(item: Seq[DataModel]) => Ok(views.html.index(item))
  //      case Left(APIError.BadAPIResponse(statusCode, message)) =>
  //        Status(statusCode)(Json.toJson(message))
  //    }
  //  }

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

  def delete(id: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.delete(id).map(_ => Accepted)

  }

  def findByName(name: String): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.findByName(name).map {
      case Right(item: DataModel) => Ok(Json.toJson(item))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
        Status(statusCode)(Json.toJson(message))
    }
  }
}


