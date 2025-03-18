package services

import connectors.LibraryConnector
import models.DataModel
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites

import java.awt.print.Book
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ApplicationService @Inject()(connector: LibraryConnector) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[DataModel] =
    connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

}
