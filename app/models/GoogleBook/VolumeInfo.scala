package models.GoogleBook

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(title: String, description: String, pageCount: Int)


object VolumeInfo {
  implicit val formats: OFormat[VolumeInfo] = Json.format[VolumeInfo]

}