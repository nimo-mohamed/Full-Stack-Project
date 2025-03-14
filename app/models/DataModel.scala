package models

import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String, name: String, description: String, pageCount: Int)


object DataModel {
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

//  val bookOne = DataModel("id1", "Book name", "Author name", 10)
//  println(bookOne.name)
}