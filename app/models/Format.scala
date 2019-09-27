package models

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

object Format {

  implicit val playerNameReads: Reads[String] = (JsPath \ "username").read[String]

  implicit val playerJoinedReads: Reads[Msg[String]] = (
    (JsPath \ "msgType").read[String] and
      (JsPath \ "obj").read[String]
  )
}