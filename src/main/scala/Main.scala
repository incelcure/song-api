import akka.actor.ActorSystem
import cats.effect.IO
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.{ClientConfiguration, Protocol}
import controllers.ControllerModule
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import services.ServiceModule
import sttp.client4.WebSocketSyncBackend
import sttp.client4.httpclient.HttpClientSyncBackend

import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  def main(args: Array[String]): Unit = {
    // Client for Auth and Enricher
    val back: WebSocketSyncBackend = HttpClientSyncBackend()
    // S3
    val awsAccessKey = System.getenv("S3_ACCESS_KEY")
    val awsSecretKey = System.getenv("S3_SECRET_KEY")
    val s3Host = System.getenv("S3_HOST")
    val bucketName = "song-bucket"

    val config = new ClientConfiguration()
    config.setProtocol(Protocol.HTTP)

    val awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
    val awsEndpoint = new EndpointConfiguration(s3Host, System.getenv("S3_REGION"))

    val amazonS3Client = AmazonS3ClientBuilder
      .standard()
      .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
      .withClientConfiguration(config)
      .withPathStyleAccessEnabled(true)
      .withEndpointConfiguration(awsEndpoint)
      .build()
    // ACTOR SYSTEM
    implicit val actorSystem: ActorSystem = ActorSystem()
    // CONFIG FOR DB
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
    // SERVICE MODULES
    val serviceModule = new ServiceModule(amazonS3Client, bucketName, back, pgConfig)
    // CONTROLLER MODULE
    val controllerModule = new ControllerModule(serviceModule)
    // SERVER
    val server = new Server(controllerModule.endpoints)
    // START SERVER
    server.start()
  }
}