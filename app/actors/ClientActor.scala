package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, WsMessage}
import play.api.Logging
import play.api.libs.json.{JsNumber, JsObject, Json}
import utils.Params

class ClientActor(out: ActorRef, supervisor: ActorRef) extends Actor with Logging {
  override def receive: Receive = {
    case wsMessage: WsMessage =>
      logger.info("wsMessage: " + wsMessage.toString)
      wsMessage.msgType match {
        case Params.PLAYER_JOINED =>
          supervisor ! ClientJoined(wsMessage.obj.as[Player])
        case Params.PLAYER_SEND_MESSAGE =>
          supervisor ! ClientSentMessage(wsMessage.obj.as[ChatMsg])
        case Params.PLAYER_READY =>
          supervisor ! ClientReady
        case Params.PLAYER_MOVE =>
      }
    case ClientUpdate(players) =>
      logger.info("players update:")
      logger.info( Json.toJson(players).toString())
      out ! WsMessage(Params.CLIENT_PLAYERS_UPDATE, Json.toJson(players))
    case ClientSentMessage(msg) =>
      logger.info("client sent:")
      logger.info(msg.toString)
      val response = WsMessage(Params.CLIENT_MESSAGE_UPDATE, Json.toJson(msg))
      //logger.info(response.toString)
      out ! response

    case ErrorNameAlreadyUsed =>
      logger.warn("ERROR : Name already used")
      out ! WsMessage(Params.ERROR_NAME_ALREADY_USED, JsObject.empty)
  }

  override def postStop(): Unit = {
    supervisor ! ClientLeft
  }
}
object ClientActor {
  def props(out: ActorRef, server: ActorRef) = {
    Props(new ClientActor(out, server))
  }
}