package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(title: Option[String], description: Option[String], pageCount: Option[Int], industryIdentifiers: Seq[IndustryIdentifier], imageLink: Option[ImageLink])


object VolumeInfo {
  implicit val formats: OFormat[VolumeInfo] = Json.format[VolumeInfo]

}