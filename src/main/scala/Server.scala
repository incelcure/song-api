import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import sttp.model.{Part, StatusCode}
import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

import scala.concurrent.{ExecutionContextExecutor, Future}
import io.circe.{Decoder, Encoder, Json}
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4.{Request, UriContext, WebSocketSyncBackend, asStringAlways, basicRequest, multipart}
import sttp.client4._

import java.io.File
import java.nio.file.Files
import java.util.Base64
import scala.io.StdIn
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import data._

import scala.util.{Failure, Success} //implicits for decode json

class Server {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  


  //
  //  val routes = uploadRoute ~ downloadRoute ~ downloadWithMetaRoute
  //
  //  val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(routes)
  //
  //  def start(): Unit = {
  //    StdIn.readLine()
  //    bindFuture
  //      .flatMap(_.unbind)
  //      .onComplete(_ => actorSystem.terminate())
  //  }
}
