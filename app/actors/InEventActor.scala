package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import models.Msg
import play.api.Logging
import spray.json.DefaultJsonProtocol._
import play.api.libs.json.JsValue

class InEventActor(out: ActorRef) extends Actor with Logging {
  import models.Format._

  override def receive: Receive = {
    case jsValue: JsValue =>

      out ! jsValue.
      logger.info(jsValue.toString())
  }
}

object InEventActor {
  private val players = collection.mutable.LinkedHashMap[String, ]

  def props(out: ActorRef) = {
    Props(new InEventActor(out))
  }
}
