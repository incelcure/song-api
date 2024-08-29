
import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl._
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route.seal
import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.util.ByteString
import akka.stream.scaladsl.Sink
import sttp.model.Part
import sttp.tapir.json.circe.jsonBody

import java.io.File
import scala.io.StdIn
import scala.util.Random

case class MultipartFileData(filename: String, file: Part[File])

object Main {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val materializer: Materializer = Materializer(actorSystem)
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

    def countCharacters(s: String): Future[Either[Unit, Int]] =
      Future.successful(Right[Unit, Int](s.length))

    val testEndpoint1 = endpoint
      .summary("testEndpoint1")
      .in("test-endpoint-1" / query[String]("test-text"))
      .get
      .out(jsonBody[Int])

    val testRoute1 = AkkaHttpServerInterpreter().toRoute(testEndpoint1.serverLogic(countCharacters))

    val testEndpoint2 = endpoint
      .summary("testEndpoint2")
      .in("test-endpoint-2" / query[String]("test-text2"))
      .get
      .out(jsonBody[Int])

    val testRoute2 = AkkaHttpServerInterpreter().toRoute(testEndpoint2.serverLogic(countCharacters))



    val routes = testRoute1 ~ testRoute2

//    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
    val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(routes)
    StdIn.readLine()

    bindFuture
      .flatMap(_.unbind)
      .onComplete(_ => actorSystem.terminate())
  }
}