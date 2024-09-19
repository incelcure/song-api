package controllers

import data.{MultipartFileData, MultipartFileWithMeta}
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.StatusCode
import sttp.tapir._
import _root_.auth.AuthEndpointBuilder
import sttp.tapir.server.ServerEndpoint

import java.nio.file.Files
import sttp.tapir.generic.auto._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import file.services.S3FileService
import enricher.services.EnricherService
import sttp.tapir.json.circe.jsonBody

class FileController(s3Client: S3FileService, authEndpointBuilder: AuthEndpointBuilder)(enricherClient: EnricherService)(implicit ex: ExecutionContext) extends TapirController[Future] {
  val baseEndpoint = authEndpointBuilder.authEndpoint

  private val uploadEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] =
    baseEndpoint
      .post
      .in("upload")
      .in(multipartBody[MultipartFileData])
      .out(statusCode)
      .serverLogic { _ =>
        multipartFileData =>
          Future {
            s3Client.upload(multipartFileData.file, multipartFileData.filename) match {
              case Success(_) => Right(StatusCode.Created)
              case Failure(_) => Left(StatusCode.BadRequest)
            }
          }
      }

  private val downloadEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] = baseEndpoint
    .summary("download file from s3")
    .in("download" / query[String]("filename"))
    .get
    .out(jsonBody[Array[Byte]])
    .serverLogic {
      _ =>
        filename =>
          Future {
            s3Client.download(filename) match {
              case Success(filedata) => Right(filedata)
              case Failure(_) => Left(StatusCode.NotFound)
            }

          }
    }

  private val downloadWithMetaEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] = baseEndpoint
    .summary("download file from s3 with meta info")
    .in("download-with-meta" / query[String]("file-id"))
    .get
    .out(multipartBody[MultipartFileWithMeta])
    .serverLogic { _ =>
      fileId =>
        Future {
          val file = s3Client.download(fileId)
          val meta = enricherClient.getMeta(fileId)
          (file, meta) match {
            case (Success(filedata), Success(metainfo)) => Right(MultipartFileWithMeta(metainfo, filedata))
            case (Success(_), Failure(_)) => Left(StatusCode.NotFound)
            case (Failure(_), Success(_)) => Left(StatusCode.NotFound)
            case (Failure(_), Failure(_)) => Left(StatusCode.NotFound)
          }
        }

    }

  override def endpoints: List[ServerEndpoint[WebSockets with AkkaStreams, Future]] =
    List(uploadEndpoint, downloadEndpoint, downloadWithMetaEndpoint)
}
