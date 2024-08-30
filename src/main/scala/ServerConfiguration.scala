import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import sttp.model.Part
import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

import scala.concurrent.{ExecutionContextExecutor, Future}
import io.circe.Json

import java.io.File
import java.nio.file.Files
import scala.io.StdIn

import sttp.tapir.json.circe._ //implicits for decode json


case class MultipartFileData(filename: String, file: Part[File])
case class MultipartFileWithMeta(meta: Json, file: Array[Byte])

class ServerConfiguration {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  val s3Client = new S3FileService
  val enricher = new EnricherService
  val dbService = new SongDBService

  val uploadEndpoint = endpoint
    .summary("upload file to S3")
    .in("upload")
    .in(multipartBody[MultipartFileData])
    .post
    .out(jsonBody[String])

  val uploadRoute = AkkaHttpServerInterpreter().toRoute(uploadEndpoint.serverLogicPure[Future] { r =>
    println(r)
    val f = Files.readAllBytes(r.file.body.toPath)
    s3Client.upload(f, r.filename)
    Right("")
  })

  val downloadEndpoint = endpoint
    .summary("download file from s3")
    .in("download" / query[String]("filename"))
    .get
    .out(jsonBody[Array[Byte]])

  val downloadRoute = AkkaHttpServerInterpreter().toRoute(downloadEndpoint.serverLogicPure[Future] { filename =>
    Right(s3Client.download(filename).get)
  })


  val downloadWithMetaEndpoint = endpoint
    .summary("download file from s3 with meta info")
    .in("download-with-meta" / query[String]("file-id"))
    .get
    .out(multipartBody[MultipartFileWithMeta])

  val downloadWithMetaRoute = AkkaHttpServerInterpreter()
    .toRoute(downloadWithMetaEndpoint.serverLogicPure[Future] { fileId =>
      val file = s3Client.download(fileId)
      val meta = enricher.getMeta(fileId)
      Right(MultipartFileWithMeta(meta, file.get))
    })

  val routes = uploadRoute ~ downloadRoute ~ downloadWithMetaRoute

  //    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
  val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(routes)

  def start(): Unit = {
    StdIn.readLine()
    bindFuture
      .flatMap(_.unbind)
      .onComplete(_ => actorSystem.terminate())
  }
}

