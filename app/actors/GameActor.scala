package actors

import akka.actor.{Actor, ActorRef, Props}
import models.Msg

class GameActor(out: ActorRef) extends Actor {
  import spray.json._
  import spray.json.DefaultJsonProtocol._
  implicit val jsonFormat = jsonFormat2(Msg[String])

  override def receive: Receive = {
    case msg: String => msg.parseJson match {
      case jsObject: JsObject => jsObject.fields.get("msgType").get match {
        case JsString(value) => value match {
          case "Join" => out ! jsObject.prettyPrint
          case "Tick" => out ! jsObject.prettyPrint
        }
      }
      case _ =>
    }

  }
}

object GameActor {
  def props(out: ActorRef) = Props(new GameActor(out))
}
