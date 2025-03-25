package models

import models.GoogleBook.Book
import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String, name: String, description: String, pageCount: Int)


object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  def toBook(dataModel: DataModel): Book = {
    Book(identifier = dataModel._id, title = dataModel.name, description = dataModel.description, pageCount = dataModel.pageCount)
  }
}