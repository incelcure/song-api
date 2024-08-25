import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.{Success, Try}

class S3FileServiceSpec extends AnyFunSuite with Matchers {
  test("Upload filename should be s3FileTest1"){
    val s3FileService = new S3FileService
    val file = "Hello world!"
    s3FileService.upload(file.getBytes(), "s3FileTest1") shouldBe Success("s3FileTest1")
  }

  test("Donwload file should be like in s3FileTest1"){
    val s3FileService = new S3FileService
    val filename = "s3FileTest1"
    s3FileService.download("s3FileTest1").get shouldBe "Hello world!".getBytes()
  }
}
