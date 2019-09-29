package models

import akka.actor.ActorRef
import play.api.libs.json.{JsPath, JsValue, Json, Reads, Writes}
import play.api.libs.functional.syntax._

case class WsMessage(msgType: String, obj: JsValue)


case class MsgPlayerJoined(username: String)
