package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{ChatMsg, Player, PlayerWithActor}
import play.api.Logging

sealed trait ClientEvent
sealed trait ErrorClient extends ClientEvent
case class ClientJoined(player: Player) extends ClientEvent
case class ClientUpdate(players: Iterable[Player]) extends ClientEvent
case object ClientReady extends ClientEvent
case object ClientLeft extends ClientEvent
case object ErrorNameAlreadyUsed extends ErrorClient
final case class ClientSentMessage(msg: ChatMsg) extends ClientEvent

class SupervisorActor extends Actor with Logging {
  override def receive: Receive = process(Map.empty[Player, ActorRef])

  def process(players: Map[Player, ActorRef]): Receive = {
    case ClientJoined(player) =>
      logger.info("Client Joined : " + player)
      println(players.toString())
      if(!players.contains(player)) {
        val newPlayers = players + (player -> sender)
        notifyAll(newPlayers, ClientUpdate(newPlayers.keys))
        context become process(newPlayers)
      }
      else {
        sender ! ErrorNameAlreadyUsed
      }
    case ClientLeft => /** TODO */
      logger.info("CLIENT LEFT")
      val player = (players.filter(_._2 equals sender()))
      logger.info(player.head._2.toString())
      logger.info(sender.toString())
      val newPlayers = players - player.keys.headOption.get
      logger.info(newPlayers.toString())
      logger.info("size: " + newPlayers.size)
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
