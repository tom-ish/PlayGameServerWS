package controllers

import actors.{GameActor, InEventActor, OutEventActor}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Source}
import javax.inject._
import play.api.Logging
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class GameController @Inject()(cc: ControllerComponents)
                              (implicit val actorSystem: ActorSystem,
                               mat: Materializer,
                               ec: ExecutionContext)
  extends AbstractController(cc) with Logging {


  def socket(): WebSocket = WebSocket.accept[JsValue, JsValue] { implicit request =>
    logger.info("socket called")
    val userInput = ActorFlow.actorRef { out => InEventActor.props(out) }
    val userOutput = ActorFlow.actorRef  { out => OutEventActor.props(out) }


    val (sink, source) = {
      val src = MergeHub.source[JsValue]
      val sk = BroadcastHub.sink[JsValue]
      src.toMat(sk)(Keep.both).run()
    }
    val flow = Flow.fromSinkAndSource(sink, source)

    userInput
      .viaMat(flow)(Keep.right)
      .viaMat(userOutput)(Keep.right)
  }

  def socketString(): WebSocket = WebSocket.accept[String, String] { implicit request =>
    logger.info("socketString called")
    val userInput = ActorFlow.actorRef { out => InEventActor.props(out) }
    val userOutput = ActorFlow.actorRef  { out => OutEventActor.props(out) }


    val (sink, source) = {
      val src = MergeHub.source[String]
      val sk = BroadcastHub.sink[String]
      src.toMat(sk)(Keep.both).run()
    }
    val flow = Flow.fromSinkAndSource(sink, source)

    userInput
      .viaMat(flow)(Keep.right)
      .viaMat(userOutput)(Keep.right)
  }

}
