package connectors

import play.api.libs.json.OFormat

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws._
//import play.api.libs.json.{Json, OFormat}

class LibraryConnector @Inject()(ws: WSClient) {
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): Future[Response] = {
    val request = ws.url(url)
    val response = request.get()
    response.map {
      result =>
        result.json.as[Response]
    }
  }
}