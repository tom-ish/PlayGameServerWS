package actors

import akka.actor.{Actor, ActorRef, Props}
import akka.event.LoggingReceive
import models.{ChatMsg, Player, PlayerJoined, PlayerWithActor, PlayersUpdate, WsMessage}
import play.api.Logging
import spray.json.DefaultJsonProtocol._
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import utils.{Params, Tools}

import scala.collection.mutable

class InEventActor(out: ActorRef) extends Actor with Logging {
  import InEventActor._

  override def receive: Receive = {
    case wsMessage: WsMessage =>
      logger.info("receive wsMessage")
      wsMessage.msgType match {
        case Params.PLAYER_JOINED =>
          val player = wsMessage.obj.as[Player]
          players += (player.name -> PlayerWithActor(player, out))
          out ! "ok"
          notifyPlayersChanged()
        case Params.PLAYER_SEND_MESSAGE =>
          playerSendMessage(wsMessage.obj.as[ChatMsg])
        case Params.PLAYER_READY =>
        case Params.PLAYER_LEFT =>
      }
    case PlayersUpdate(players) =>
      logger.info("receive playersUpdate message")
      self ! "ok2"
    case s: String => logger.info(s)
  }

  def playerJoinedHandler(p: Player, from: ActorRef) = {
    println("player joined")
    println(p.name)
    players += (p.name -> PlayerWithActor(p, out))
  }

  def notifyPlayersChanged() = {
    println("notifying to all players")
    players.values.foreach {  // notify to all the other players that there is a new player
      p =>
        logger.info("player => " + p.player.name)
        p.actor ! PlayersUpdate(players.values.map(_.player))
    }
  }

  def updatePlayers(name: String, actorRef: ActorRef) = {
    if(!players.contains(name))
      players += (name -> PlayerWithActor(Player(name), actorRef))
  }

  def playerSendMessage(msg: ChatMsg) = {
    println("player sent message")
    println(msg)
  }

  def playerReadyHandler() = { ??? }
  def playerLeftHandler() = {???}
}

object InEventActor {
  val players = collection.mutable.LinkedHashMap[String, PlayerWithActor]()
  val messages = collection.mutable.Queue[ChatMsg]()

  def props(out: ActorRef) = {
    println("props")
    Props(new InEventActor(out))
  }
}

