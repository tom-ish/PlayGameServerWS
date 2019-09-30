package services

import java.util.concurrent.atomic.AtomicReference

import akka.actor.ActorSystem
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink}
import javax.inject.Inject
import models.{ChatRoom, ChatRoomRepository, WsMessage}

import scala.concurrent.duration._

import scala.collection.mutable

class ChatRoomService @Inject()(implicit val materializer: Materializer, system: ActorSystem) extends ChatRoomRepository {
  import ChatRoomService._
  override def chatRoom(roomId: String, username: String): ChatRoom = {
    roomPool.get.get(roomId) match {
      case Some(chatRoom) => chatRoom
      case None =>
        val room = create(roomId)
        roomPool.get() += (roomId -> room)
        room
    }
  }

  private def create(roomId: String): ChatRoom = {
    val (sink, source) = MergeHub.source[WsMessage](perProducerBufferSize = 16)
      .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
      .run()

    source.runWith(Sink.ignore)

    val bus: Flow[WsMessage, WsMessage, UniqueKillSwitch] = Flow.fromSinkAndSource(sink, source)
      .joinMat(KillSwitches.singleBidi[WsMessage, WsMessage])(Keep.right)
      .backpressureTimeout(3.seconds)
      .map { e =>
        println(s"$e")
        e
      }

    ChatRoom(roomId, bus)
  }
}

object ChatRoomService {
  private var rooms = scala.collection.mutable.Map[String, ChatRoom]()

  val roomPool: AtomicReference[scala.collection.mutable.Map[String, ChatRoom]] = new AtomicReference[mutable.Map[String, ChatRoom]](rooms)
}