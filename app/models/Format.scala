package models

import play.api.libs.json.{JsPath, JsValue, Json, Reads, Writes}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.MessageFlowTransformer
import utils.Params


case class Msg(msgType: String, obj: JsValue)

object Format {

  implicit val playerWrites = new Writes[Player] {
    override def writes(p: Player): JsValue = Json.obj(
      Params.PLAYER_FIELD_NAME -> p.name
    )

  }
  implicit val playerReads: Reads[Player] = (
    (JsPath \ Params.PLAYER_FIELD_NAME).read[String] map (name => Player(name))
  )

  implicit val msgWrites = new Writes[WsMessage] {
    override def writes(msg: WsMessage) = Json.obj(
      Params.MSG_FIELD_MSG_TYPE -> msg.msgType,
      Params.MSG_FIELD_OBJ -> msg.obj
    )
  }

  implicit val msgReads: Reads[WsMessage] = (
    (JsPath \ Params.MSG_FIELD_MSG_TYPE).read[String] and
      (JsPath \ Params.MSG_FIELD_OBJ).read[JsValue]
    ) (WsMessage.apply _)
}