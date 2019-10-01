package controllers

import actors.{ClientActor, InEventActor, OutEventActor, SupervisorActor}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import akka.stream.{KillSwitches, Materializer}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import javax.inject._
import models.WsMessage
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.{ChatRoomService, ChatService}
import utils.Params

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class GameController @Inject()(cc: ControllerComponents)
                              (implicit val actorSystem: ActorSystem,
                               mat: Materializer,
                               chatRoomService: ChatRoomService,
                               ec: ExecutionContext)
  extends AbstractController(cc) with Logging {





  import models.Format._
  implicit val wsMessageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[WsMessage, WsMessage]
//  def socket(): WebSocket = WebSocket.accept[WsMessage, WsMessage] { implicit request =>
//    logger.info("socket called")
//    val userInput = ActorFlow.actorRef { out => InEventActor.props(out) }
//    val userOutput = ActorFlow.actorRef  { out => OutEventActor.props(out) }
//
//    val (sink, source) = {
//      val src = MergeHub.source[WsMessage]
//      val sk = BroadcastHub.sink[WsMessage]
//      src.toMat(sk)(Keep.both).run()
//    }
//    val flow = Flow.fromSinkAndSource(sink, source)
//
//    userInput
//      .viaMat(flow)(Keep.right)
//      .viaMat(userOutput)(Keep.right)
//  }

  val supervisor = actorSystem.actorOf(Props[SupervisorActor], "supervisor")

  def gameSocket(): WebSocket = WebSocket.accept[WsMessage, WsMessage] { implicit request =>
    logger.info("gamesocket called")
    val userInput = ActorFlow.actorRef[WsMessage, WsMessage] { out => ClientActor.props(out, supervisor) }
    val userOuput = ActorFlow.actorRef[WsMessage, WsMessage] { out => OutEventActor.props(out) }

    val (sink, source) = {
      val src = MergeHub.source[WsMessage](perProducerBufferSize = 16)
      val sk = BroadcastHub.sink[WsMessage](bufferSize = 256)
      src.toMat(sk)(Keep.both).run()
    }
    source.runWith(Sink.ignore)

    val flow = Flow.fromSinkAndSource(sink, source)
        .joinMat(KillSwitches.singleBidi[WsMessage, WsMessage])(Keep.right)
        .backpressureTimeout(3.seconds)
        .map { e =>
          logger.info(s"$e")
          e
        }

    userInput
      .viaMat(flow)(Keep.right)
      .viaMat(userOuput)(Keep.right)


  }

}
