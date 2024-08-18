import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.{Success, Try}

class SystemFileSvcSpec extends AnyFunSuite with Matchers {
    test("Upload filename should be fileTest1") {
      val sysFileSvc = new SystemFileSvc()
      val file = "Hello world!"
      sysFileSvc.upload(file.getBytes, "fileTest1") shouldBe Success("fileTest1")
    }

    test("Download file should be same byte Array"){
      val sysFileSvc = new SystemFileSvc()
      sysFileSvc.download("fileTest1").get shouldBe "Hello world!".getBytes
    }

    test("Upload file byte array should be the same byteArray"){
      val sysFileSvc = new SystemFileSvc()
      val file = "Hello world test 2"
      sysFileSvc.upload(file.getBytes, "fileTest2")
      sysFileSvc.download("fileTest2").get shouldBe file.getBytes
    }

}