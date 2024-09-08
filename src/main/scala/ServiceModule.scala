import com.amazonaws.services.s3.AmazonS3

class ServiceModule(/** dependecies**/ s3Client: AmazonS3) {
  val s3FileService = new S3FileService(??? , ???)
}
