package actors

import akka.actor.{Actor, ActorRef, Props}

class OutEventActor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: String =>
      println("outEventActor : ")
      println(msg)
  }
}

object OutEventActor {
  def props(out: ActorRef) = Props(new InEventActor(out))
}
