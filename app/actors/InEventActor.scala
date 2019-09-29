package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import models.{WsMessage, Player, PlayerWithActor}
import play.api.Logging
import spray.json.DefaultJsonProtocol._
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import utils.{Params, Tools}

import scala.collection.mutable

class InEventActor(out: ActorRef) extends Actor with Logging {
  import InEventActor._
  import models.Format._

  override def receive: Receive = {
    case wsMessage: WsMessage =>
      logger.info("receive wsMessage")
      wsMessage.msgType match {
        case Params.PLAYER_JOINED =>
          playerJoinedHandler(wsMessage.obj.as[Player])
        case Params.PLAYER_READY =>
        case Params.PLAYER_LEFT =>
      }
  }

  def playerJoinedHandler(p: Player) = {

    println("player joined")
    println(p.name)
    players += (p.name -> PlayerWithActor(p, out))
    players.foreach {
      player => if(!(player._1 equals p.name)) println(player._1)
    }
//    obj.validate[Player] match {
//      case s: JsSuccess[Player] =>
//        val player = s.get
//      case JsError(errors) =>
//    }
  }

  def playerReadyHandler() = { ??? }
  def playerLeftHandler() = {???}
}

object InEventActor {
  val players = collection.mutable.LinkedHashMap[String, PlayerWithActor]()

  def props(out: ActorRef) = {
    println("props")
    Props(new InEventActor(out))
  }
}

