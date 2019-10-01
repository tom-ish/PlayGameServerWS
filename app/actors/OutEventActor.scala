package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.{ChatMsg, WsMessage}

class OutEventActor(out: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg: ChatMsg =>
      log.info("out: "+msg)
      log.info(msg.toString)
    case msg: WsMessage =>
      log.info(msg.toString)
      out ! msg
    case e =>
      log.info("receive e : ")
      log.info(e.toString)
  }
}

object OutEventActor {
  def props(out: ActorRef) = Props(new OutEventActor(out))
}
