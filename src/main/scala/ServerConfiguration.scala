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

import scala.util.{Failure, Success} //implicits for decode json

case class MultipartFileData(filename: String, file: Part[File])

case class MultipartFileWithMeta(meta: Json, file: Array[Byte])

case class AuthError(message: String)

class ServerConfiguration extends AuthCheck {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(actorSystem)
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
  val dbService = new SongDBService

  val s3Client = new S3FileService
  val enricher = new EnricherService

  val uploadEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] = endpoint
    .summary("upload file to S3")
    .in("upload")
    .in(multipartBody[MultipartFileData])
    .in(header[String]("Authorization"))
    .post
    .errorOut(statusCode)
    .out(statusCode)
    .serverLogic[Future] { request_data =>
      val (fileData, authBase) = request_data
      Future {
        if (!checkCreds(authBase)) {
          Left(StatusCode.Unauthorized)
        }
        else {
          val file = Files.readAllBytes(fileData.file.body.toPath)
          s3Client.upload(file, fileData.filename) match {
            case Success(filename) => Right(StatusCode.Created)
            case Failure(_) => Left(StatusCode.BadRequest)
          }
        }
      }
    }


  val uploadRoute = AkkaHttpServerInterpreter().toRoute(uploadEndpoint)

  val downloadEndpoint = endpoint
    .summary("download file from s3")
    .in("download" / query[String]("filename"))
    .in(header[String]("Authorization"))
    .get
    .out(jsonBody[Array[Byte]])
    .errorOut(statusCode)
    .serverLogic[Future] {
      request_data =>
        val (filename, authBase) = request_data
        Future {
          if (!checkCreds(authBase)) {
            Left(StatusCode.Unauthorized)
          }
          else {
            s3Client.download(filename) match {
              case Success(filedata) => Right(filedata)
              case Failure(_) => Left(StatusCode.NotFound)
            }
          }
        }
    }


  val downloadRoute = AkkaHttpServerInterpreter().toRoute(downloadEndpoint)


  val downloadWithMetaEndpoint = endpoint
    .summary("download file from s3 with meta info")
    .in("download-with-meta" / query[String]("file-id"))
    .in(header[String]("Authorization"))
    .get
    .out(multipartBody[MultipartFileWithMeta])
    .errorOut(statusCode)
    .serverLogic[Future] {
      request_data =>
        Future {
          val (fileId, authBase) = request_data
          if (!checkCreds(authBase)) {
            Left(StatusCode.Unauthorized)
          }
          else {
            val file = s3Client.download(fileId)
            val meta = enricher.getMeta(fileId)
            (file, meta) match {
              case (Success(filedata), Success(metainfo)) => Right(MultipartFileWithMeta(metainfo, filedata))
              case (Success(_), Failure(_)) => Left(StatusCode.NotFound)
              case (Failure(_), Success(_)) => Left(StatusCode.NotFound)
              case (Failure(_), Failure(_)) => Left(StatusCode.NotFound)
            }
          }
        }
    }

  val downloadWithMetaRoute = AkkaHttpServerInterpreter().toRoute(downloadWithMetaEndpoint)

  val routes = uploadRoute ~ downloadRoute ~ downloadWithMetaRoute
  //  val routes = uploadRoute ~ downloadRoute

  //    val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
  val bindFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(routes)

  def start(): Unit = {
    StdIn.readLine()
    bindFuture
      .flatMap(_.unbind)
      .onComplete(_ => actorSystem.terminate())
  }
}
