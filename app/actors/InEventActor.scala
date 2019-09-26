package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import play.api.Logging
import play.api.libs.json.JsValue

class InEventActor(out: ActorRef) extends Actor with Logging {
  override def receive: Receive = {
    case jsValue: JsValue =>
      logger.info("received jsValue")
      logger.info(jsValue.toString())
    case msg: String =>
      logger.info("received string")
      logger.info(msg.toString())
  }
}

object InEventActor {
  def props(out: ActorRef) = Props(new InEventActor(out))
}
