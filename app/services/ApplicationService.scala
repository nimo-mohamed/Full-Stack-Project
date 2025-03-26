package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}
import models.GoogleBook.BookList
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ApplicationService @Inject()(connector: LibraryConnector) {
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, BookList] =
    connector.get[BookList](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

}


