package controllers

import actors.MyActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject._
import play.api._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents)
                              (implicit val actorSystem: ActorSystem,
                               mat: Materializer)
  extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    val url = routes.GameController.gameSocket().webSocketURL()
    Ok(views.html.index(url))
  }
}
