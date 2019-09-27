package models

import akka.actor.ActorRef
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class Msg[T](msgType: String, obj: T)
case class MsgPlayerJoined(username: String)
