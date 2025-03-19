package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class Book(volumeInfo: VolumeInfo)

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]

}