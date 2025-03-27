package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class ImageLink(smallThumbnail: String, thumbnail: String)

object ImageLink {
  implicit val formats: OFormat[ImageLink] = Json.format[ImageLink]

}