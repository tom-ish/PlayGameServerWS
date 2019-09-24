package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{JsValue, Json}

class MyActor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case a: String =>
      println(s"receive")
      println(s"$a")
      out ! Json.toJson(a)
    case js: JsValue =>
      println(js.toString())
      out ! js
  }
}
object MyActor {
  def props(out: ActorRef) = Props(new MyActor(out))
}