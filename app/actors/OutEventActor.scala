package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.{ChatMsg, WsMessage}

class OutEventActor(out: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg: ChatMsg =>
      println(msg)
      log.info(msg.toString)
    case msg: WsMessage =>
      println("outEventActor : ")
      println(msg)
  }
}

object OutEventActor {
  def props(out: ActorRef) = Props(new InEventActor(out))
}
