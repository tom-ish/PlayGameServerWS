package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, PlayerWithActor}
import play.api.Logging

sealed trait ClientEvent
case class ClientJoined(player: Player) extends ClientEvent
case class ClientUpdate(players: Iterable[Player]) extends ClientEvent
case object ClientReady extends ClientEvent
case object ClientLeft extends ClientEvent
final case class ClientSentMessage(msg: ChatMsg) extends ClientEvent

class SupervisorActor extends Actor with Logging {
  override def receive: Receive = process(Map.empty[Player, ActorRef])

  def process(players: Map[Player, ActorRef]): Receive = {
    case ClientJoined(player) =>
      logger.info("Client Joined : " + player)
      println(sender().toString())
      val newPlayers = if(!players.contains(player)) players + (player -> sender) else players
      notifyAll(newPlayers, ClientUpdate(newPlayers.keys))
      context become process(newPlayers)
    case ClientLeft => /** TODO */
      val player = (players.filter(_._2 equals sender()))
      val newPlayers = players - player.keys.headOption.get
      notifyAll(newPlayers, ClientLeft)
      context become process(newPlayers)
    case csm: ClientSentMessage =>
      logger.info("Client sent Message : " + csm)
     // (players - sender).foreach(_._1 ! csm.msg)
      (players).foreach(_._2 ! csm)
  }

  private def notifyAll(players: Map[Player, ActorRef], event: ClientEvent) = players.foreach(_._2 ! event)
}

object SupervisorActor {
//  val players = collection.mutable.LinkedHashMap[String, PlayerWithActor]()
//  val messages = collection.mutable.Queue[ChatMsg]()
  def props = Props(new SupervisorActor())
}
