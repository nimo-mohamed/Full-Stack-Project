package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(title: String, description: String, pageCount: Int, industryIdentifier: Seq[IndustryIdentifier], imageLink: ImageLink)


object VolumeInfo {
  implicit val formats: OFormat[VolumeInfo] = Json.format[VolumeInfo]

}