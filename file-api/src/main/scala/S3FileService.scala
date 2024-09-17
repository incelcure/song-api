import com.amazonaws.{ClientConfiguration, Protocol}
import com.amazonaws.auth.{AWSCredentialsProvider, AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client, AmazonS3ClientBuilder, S3ClientOptions}
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util.{Failure, Success, Try, Using}

class S3FileService(amazonS3Client: AmazonS3, bucketName: String) extends FileService {
//  private val awsAccessKey = System.getenv("S3_ACCESS_KEY")
//  private val awsSecretKey = System.getenv("S3_SECRET_KEY")
//  private val s3Host = System.getenv("S3_HOST")
//  private val bucketName = "song-bucket"
//
//  private val config = new ClientConfiguration()
//  config.setProtocol(Protocol.HTTP)
//
//  private val awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
//  private val awsEndpoint = new EndpointConfiguration(s3Host, System.getenv("S3_REGION"))
//
//  private val amazonS3Client = AmazonS3ClientBuilder
//    .standard()
//    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//    .withClientConfiguration(config)
//    .withPathStyleAccessEnabled(true)
//    .withEndpointConfiguration(awsEndpoint)
//    .build()

  override def upload(file: Array[Byte], filename: String): Try[String] = Try {
    if (!amazonS3Client.doesBucketExistV2(bucketName)) {
      amazonS3Client.createBucket(bucketName)
    }
    val tempFile = File.createTempFile("upload-", filename)
    Using.resource(new FileOutputStream(tempFile)) { stream =>
      stream.write(file)
      amazonS3Client.putObject(new PutObjectRequest(bucketName, filename, tempFile))
      filename
    }
  }

  override def download(filename: String): Try[Array[Byte]] =
    amazonS3Client.doesObjectExist(bucketName, filename) match {
      case true => {
        val obj = amazonS3Client.getObject(bucketName, filename)
        Success(IOUtils.toByteArray(obj.getObjectContent))
      }
      case false => Failure(new Exception("File not found"))
    }

}
