package services

import com.amazonaws.services.s3.AmazonS3
import auth.{AuthClient, AuthClientImpl}
import sttp.client4.WebSocketSyncBackend

import file.services.S3FileService
import enricher.services.EnricherService
import db.repositories.SongDBService

import scala.concurrent.ExecutionContext.Implicits.global

class ServiceModule(s3Client: AmazonS3, s3BucketName: String, httpClient: WebSocketSyncBackend) {
  val authClient: AuthClient = new AuthClientImpl(httpClient)
  val s3FileService = new S3FileService(s3Client, s3BucketName)
  val enricherService: EnricherService = new EnricherService
  val songDBService: SongDBService = new SongDBService
}