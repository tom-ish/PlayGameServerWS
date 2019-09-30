package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, WsMessage}
import play.api.Logging
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
      logger.info(msg.toString)
      out ! msg
  }
}
object ClientActor {
  def props(out: ActorRef, server: ActorRef) = {
    Props(new ClientActor(out, server))
  }
}