package file.services

import com.amazonaws.{ClientConfiguration, Protocol}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Success

class S3FileServiceSpec extends AnyFunSuite with Matchers {
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

  test("Upload filename should be s3FileTest1") {

    val s3FileService = new S3FileService(amazonS3Client, bucketName)
    val file = "Hello world!"
    s3FileService.upload(file.getBytes(), "bruh322.txt") shouldBe Success("bruh322.txt")
  }

  test("Donwload file should be like in s3FileTest1") {
    val s3FileService = new S3FileService(amazonS3Client, bucketName)
    s3FileService.download("bruh322.txt").get shouldBe "Hello world!".getBytes()
  }
}
