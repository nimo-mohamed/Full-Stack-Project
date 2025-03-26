package models.GoogleBook

import models.DataModel
import play.api.libs.json.{Json, OFormat}

case class Book(volumeInfo: VolumeInfo)

case class BookList(items: List[Book])

object BookList {
  implicit val formats: OFormat[BookList] = Json.format[BookList]
}

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]

//  def toDataModel(book: Book): DataModel = {
//    DataModel(_id = book.volumeInfo.industryIdentifier.identifier, name = book.volumeInfo.title, description = book.volumeInfo.description, pageCount = book.volumeInfo.pageCount)
//  }

}