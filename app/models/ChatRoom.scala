package models

import akka.stream.UniqueKillSwitch
import akka.stream.scaladsl.Flow

case class ChatRoom(roomId: String, bus: Flow[WsMessage, WsMessage, UniqueKillSwitch])

trait ChatRoomRepository {
  def chatRoom(roomId: String, username: String): ChatRoom
}