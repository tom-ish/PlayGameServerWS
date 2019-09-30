package services

import javax.inject.Inject
import models.{ChatRoom, ChatRoomRepository}

class ChatService @Inject()(repository: ChatRoomRepository) {
  def start(roomId: String, usernme: String): ChatRoom = repository.chatRoom(roomId, usernme)
}