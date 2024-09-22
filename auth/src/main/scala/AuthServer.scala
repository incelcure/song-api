import AuthServer.AkkaEndpoint
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.Future

class AuthServer(endpoints: List[AkkaEndpoint])(implicit actorSystem: ActorSystem) {
  import actorSystem._

  private val routes = AkkaHttpServerInterpreter().toRoute(endpoints)

  def start(): Future[Unit] = Http()
    .newServerAt("localhost", 8081)
    .bind(routes)
    .flatMap(_ => Future.never)
}

object AuthServer{
  type AkkaEndpoint = ServerEndpoint[WebSockets with AkkaStreams, Future]
}
