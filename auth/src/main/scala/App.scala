import akka.actor.ActorSystem
import akka.stream.Materializer
import cats.effect.IO
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{endpoint, multipartBody}
import sttp.tapir.server.ServerEndpoint

import java.nio.file.Files
import scala.concurrent.{ExecutionContextExecutor, Future}

object Main {
  def main(args: Array[String]): Unit = {
    val pgUrl = System.getenv("POSTGRES_DB_URL")
    val pgUser = System.getenv("POSTGRES_USER")
    val pgPassword = System.getenv("POSTGRES_PASSWORD")

    val pgConfig: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = s"jdbc:postgresql://$pgUrl",
      user = pgUser,
      password = pgPassword,
      logHandler = None
    )
    implicit val actorSystem: ActorSystem = ActorSystem()
    val authService = new AuthService(pgConfig)
    val authController = new AuthController(authService)
    val authServer = new AuthServer(authController.endpoints)
    authServer.start()
  }
}