import akka.actor.ActorSystem
import akka.stream.Materializer
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{endpoint, multipartBody}
import sttp.tapir.server.ServerEndpoint

import java.nio.file.Files
import scala.concurrent.{ExecutionContextExecutor, Future}

object Main {
  def main(args: Array[String]): Unit = {
    println("Hey, auth!")
    val authController = new AuthController

    authController.run()
  }
}