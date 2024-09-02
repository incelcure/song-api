import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.post
import akka.stream.Materializer

import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.model.{Part, StatusCode}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

case class User(name: String, password: String)

class AuthController {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher



//  val registerEndoint = endpoint
//    .summary("register user")
//    .in("register")
//    .in(jsonBody[String])
//    .post
//    .out(jsonBody[String])
//    .serverLogicSuccess {}

//  val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8081).bind(routes)
//
//  def start(): Unit = {
//    StdIn.readLine()
//    bindFuture
//      .flatMap(_.unbind)
//      .onComplete(_ => actorSystem.terminate())
//  }
}
