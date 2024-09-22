package services

import com.amazonaws.services.s3.AmazonS3
import auth.{AuthClient, AuthClientImpl}
import cats.effect.IO
import sttp.client4.WebSocketSyncBackend
import file.services.S3FileService
import enricher.services.EnricherService
import db.repositories.SongDBRepository
import doobie.util.transactor.Transactor.Aux

import scala.concurrent.ExecutionContext.Implicits.global

class ServiceModule(s3Client: AmazonS3, s3BucketName: String, httpClient: WebSocketSyncBackend, pgConfig: Aux[IO, Unit]) {
  val authClient: AuthClient = new AuthClientImpl(httpClient)
  val s3FileService = new S3FileService(s3Client, s3BucketName)
  val enricherService: EnricherService = new EnricherService
  val songDBRepository: SongDBRepository = new SongDBRepository(pgConfig)
}