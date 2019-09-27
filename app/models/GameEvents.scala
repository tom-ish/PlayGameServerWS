package models

import akka.actor.ActorRef

sealed trait GameEvent
case class PlayerJoined(playername: String, actorRef: ActorRef) extends GameEvent
