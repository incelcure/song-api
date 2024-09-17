package controllers
import data.MultipartFileData
import sttp.capabilities.WebSockets
import sttp.capabilities.akka.AkkaStreams
import sttp.model.StatusCode
import sttp.tapir._
import auth.AuthEndpointBuilder
import sttp.tapir.server.ServerEndpoint

import java.nio.file.Files
import sttp.tapir.generic.auto._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


class FileController(s3Client : S3FileService, authEndpointBuilder: AuthEndpointBuilder)(implicit ex : ExecutionContext) extends TapirController[Future] {
  val baseEndpoint = authEndpointBuilder.authEndpoint

  val uploadEndpoint: ServerEndpoint[WebSockets with AkkaStreams, Future] =
    baseEndpoint
      .post
      .in("upload")
      .in(multipartBody[MultipartFileData])
      .out(statusCode)
      .serverLogic { _ => multipartFileData =>
        Future {
          s3Client.upload(multipartFileData.file, multipartFileData.filename) match {
            case Success(_) => Right(StatusCode.Created)
            case Failure(_) => Left(StatusCode.BadRequest)
          }
        }
      }

  override def endpoints: List[ServerEndpoint[WebSockets with AkkaStreams, Future]] = List(uploadEndpoint)
}
