package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class IndustryIdentifier(`type`: String, identifier: String)

object IndustryIdentifier {
  implicit val formats: OFormat[IndustryIdentifier] = Json.format[IndustryIdentifier]

}