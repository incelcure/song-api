import AuthController.Credentials
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives._
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
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder


class AuthController {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  private val authService = new AuthService


  val registerEndoint = endpoint
    .summary("register user")
    .in("register")
    .in(jsonBody[Credentials])
    .post
    .out(jsonBody[String])
    .serverLogicSuccess { creds =>
      Future.fromTry {
        authService.register(creds.name, creds.password)
      }
    }

  val regRoute = AkkaHttpServerInterpreter().toRoute(registerEndoint)

  val loginEndpoint = endpoint
    .summary("login user")
    .in("login")
    .in(jsonBody[Credentials])
    .get
    .out(jsonBody[Boolean])
    .serverLogic{ creds =>
      Future.fromTry{
        authService.login(creds.name, creds.password)
      }
    }
  val logRoute = AkkaHttpServerInterpreter().toRoute(loginEndpoint)

  val routes = regRoute ~ logRoute
  val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8081).bind(routes)
  def run(): Unit = {
    StdIn.readLine()
    bindFuture
      .flatMap(_.unbind)
      .onComplete(_ => actorSystem.terminate())
  }
}

object AuthController{
  case class Credentials(name: String, password: String)

  implicit val credentialsDecoder: Decoder[Credentials] = deriveDecoder
}