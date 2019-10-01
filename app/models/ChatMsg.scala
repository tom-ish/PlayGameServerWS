package models

import play.api.libs.json.{Json, Writes}
import utils.Params

case class ChatMsg(text: String, user: String, date: String)
object ChatMsg {
  implicit val chatMsgReads = Json.reads[ChatMsg]
  implicit val chatMsgWrites = Json.writes[ChatMsg]
//  implicit val msgWrites = new Writes[WsMessage] {
//    override def writes(msg: WsMessage) = Json.obj(
//      Params.MSG_FIELD_MSG_TYPE -> msg.msgType,
//      Params.MSG_FIELD_OBJ -> msg.obj
//    )
//  }
}
