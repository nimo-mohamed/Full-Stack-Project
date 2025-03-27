package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.json.{JsError, JsSuccess, OFormat}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws._

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response
        .map {
          result =>
            result.json.validate[Response] match {
              case JsSuccess(bookList, _) => Right(bookList)
              case JsError(errors) => Left(APIError.BadAPIResponse(500, s"Could not connect: $errors"))
            }
        }
    }
  }
}
