import sttp.capabilities
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.server.ServerEndpoint

import scala.concurrent.Future

class BlaBlaController extends TapirController[Future] {






  override def endpoints: List[ServerEndpoint[capabilities.WebSockets with AkkaStreams, Future]] = ???
}
