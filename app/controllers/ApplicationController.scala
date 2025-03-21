package controllers

// import akka.io.dns.internal.DnsClient.DnsQuestion

import models.{APIError, DataModel, GoogleBook}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.ApplicationService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, val service: ApplicationService)(implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok(Json.toJson(item))
      case Left(APIError.BadAPIResponse(statusCode, message)) =>
              Status(statusCode)(Json.toJson(message))
    }
  }

  //case Left(error: APIError) => Status(error.BadAPIResponse(404))(Json.toJson("Unable to find any books"))
  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.read(id).map { data =>
      Ok(Json.toJson(data))
    }.recover {
      case _: NoSuchElementException =>
        NotFound(Json.toJson(s"Unable to find data for ID: $id"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => dataRepository.update(id, dataModel).flatMap {
        _ =>
          dataRepository.read(id).map(book => Accepted {
            Json.toJson(book)
          })
      }
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) => dataRepository.delete(id).map(_ => Accepted)
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

  //Left(error) => APIError.BadAPIResponse

//  {
//    case _: NoSuchElementException =>
//      NotFound(Json.toJson("error" -> s"Unable to find book: $term"))
//  }

  // def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
  //    service.getGoogleBook(search = search, term = term).value.map {
  //      case Right(book) => ??? //Hint: This should be the same as before
  //      case Left(error) => ???
  //    }
  //  }


}


