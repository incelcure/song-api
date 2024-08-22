import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util.{Try, Using}

class S3FileService extends FileService {

  override def upload(file: Array[Byte], filename: String): Try[String] = Try{
    val awsCredentials = new BasicAWSCredentials(System.getenv("S3_ACCESS_KEY"), System.getenv("S3_SECRET_KEY"))
    val amazonS3Client = new AmazonS3Client(awsCredentials)

    val BUCKET_NAME = "song-api-bucket"

    amazonS3Client.createBucket(BUCKET_NAME)
    val s3File = new File(filename)

    amazonS3Client.putObject(BUCKET_NAME, filename, s3File)
    ""
  }

  override def download(filename: String): Try[Array[Byte]] = ???
}
