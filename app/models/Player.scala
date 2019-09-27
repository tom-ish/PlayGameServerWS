package models

import akka.actor.ActorRef

case class Player(name: String, color: String)
case class PlayerWithActor(player: Player, actor: ActorRef)
