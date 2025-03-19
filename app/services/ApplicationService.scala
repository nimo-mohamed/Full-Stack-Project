package services

import connectors.LibraryConnector
import models.DataModel
import models.GoogleBook.Book
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ApplicationService @Inject()(connector: LibraryConnector) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): Future[Book] =
    connector.get[Book](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

}
