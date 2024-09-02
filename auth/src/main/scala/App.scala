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
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val materializer: Materializer = Materializer(actorSystem)
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

//    val uploadEndpoint : ServerEndpoint[WebSockets with AkkaStreams, Future] = endpoint
//      .summary("upload file to S3")
//      .in("upload")
//      .in(multipartBody[MultipartFileData])
//      .post
//      .out(jsonBody[String])
//      .serverLogicSuccess { r =>
//
//        }
//      }
  }
}