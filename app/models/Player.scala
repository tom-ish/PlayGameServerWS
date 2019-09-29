package models

import akka.actor.ActorRef
import play.api.libs.json.Json

case class Player(name: String)
case object Player {
  implicit val playerReads = Json.reads[Player]
}
case class PlayerWithActor(player: Player, actor: ActorRef)
case class Position(x: Int, y: Int)