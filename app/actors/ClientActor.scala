package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, WsMessage}
import play.api.Logging
import play.api.libs.json.Json
import utils.Params

class ClientActor(out: ActorRef, supervisor: ActorRef) extends Actor with Logging {
  override def receive: Receive = {
    case wsMessage: WsMessage =>
      logger.info(wsMessage.toString)
      wsMessage.msgType match {
        case Params.PLAYER_JOINED =>
          supervisor ! ClientJoined(wsMessage.obj.as[Player])
        case Params.PLAYER_SEND_MESSAGE =>
          supervisor ! ClientSentMessage(wsMessage.obj.as[ChatMsg])
        case Params.PLAYER_READY =>
          supervisor ! ClientReady
      }
    case ClientSentMessage(msg) =>
      logger.info("client sent:")
      logger.info(msg.toString)
      val response = WsMessage(Params.CLIENT_RESPONSE, Json.toJson(msg))
      logger.info(response.toString)
      out ! response
  }
}
object ClientActor {
  def props(out: ActorRef, server: ActorRef) = {
    Props(new ClientActor(out, server))
  }
}