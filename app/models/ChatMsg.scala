package models

import play.api.libs.json.Json

case class ChatMsg(text: String, user: String, date: String)
object ChatMsg {
  implicit val chatMsgFormat = Json.reads[ChatMsg]
}
