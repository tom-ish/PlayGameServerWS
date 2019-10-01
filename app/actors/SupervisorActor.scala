package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, PlayerWithActor}
import play.api.Logging

case class ClientJoined(player: Player)
case object ClientReady
case object ClientLeft
final case class ClientSentMessage(msg: ChatMsg)

class SupervisorActor extends Actor with Logging {
  override def receive: Receive = process(Map.empty[ActorRef, Player])

  def process(players: Map[ActorRef, Player]): Receive = {
    case ClientJoined(player) =>
      logger.info("Client Joined : " + player)
      context become process(players + (sender() -> player))

    case ClientLeft =>
      context become process(players - sender())

    case csm: ClientSentMessage =>
      logger.info("Client sent Message : " + csm)
     // (players - sender).foreach(_._1 ! csm.msg)
      (players).foreach(_._1 ! csm.msg)
  }
}

object SupervisorActor {
//  val players = collection.mutable.LinkedHashMap[String, PlayerWithActor]()
//  val messages = collection.mutable.Queue[ChatMsg]()
  def props = Props(new SupervisorActor())
}
