import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

trait TapirController[F[_]] {
  def endpoints : List[ServerEndpoint[WebSockets with AkkaStreams, F]]
}
