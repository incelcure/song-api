import Server.AkkaEndpoint
import akka.actor.{ActorSystem, actorRef2Scala}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import data.{MultipartFileData, MultipartFileWithMeta}
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import java.nio.file.Files
import scala.concurrent.{ExecutionContextExecutor, Future}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.json.circe._
import sttp.client4.{Request, UriContext, WebSocketSyncBackend, asStringAlways, basicRequest, multipart}
import sttp.client4._

import scala.io.StdIn
import scala.util.{Failure, Success} //implicits for decode json




class Server(endpoints : List[AkkaEndpoint])(implicit actorSystem: ActorSystem) {
  import actorSystem._


  val dbService = new SongDBService

  val s3Client = new S3FileService
  val enricher = new EnricherService





  val uploadEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] =
    endpoint
    .post
    .in("upload")
    .in(multipartBody[MultipartFileData])
    .in(header[String]("Authorization"))
    .errorOut(statusCode)
    .out(statusCode)
    .serverLogic { case (fileData, basicAuth) =>
      Future {
        if (!authenticate(basicAuth)) {
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
          if (!authenticate(authBase)) {
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
          if (!authenticate(authBase)) {
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


  def start(): Future[Unit] = Http()
    .newServerAt("localhost", 8080)
    .bind(routes)
    .flatMap(_ => Future.never)
}

object Server {
  type AkkaEndpoint = ServerEndpoint[WebSockets with AkkaStreams , Future]
}