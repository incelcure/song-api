import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{ExecutionContext, Future}

class ServerConfiguration {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http().newServerAt("localhost", 8080).connectionSource()
  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection => // foreach materializes the source
      println("Accepted new connection from " + connection.remoteAddress)
      // ... and then actually handle the connection
    }).run()
}

