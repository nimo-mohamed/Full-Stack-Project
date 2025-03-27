package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class Author(authors: Seq[String])

object Author {
  implicit val formats: OFormat[Author] = Json.format[Author]

}

