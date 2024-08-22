import java.io.{File, FileInputStream, FileOutputStream}
import scala.util
import scala.util.{Try, Using}

class SystemFileSvc extends FileSvc {

  override def upload(file: Array[Byte], filename: String): Try[String] = Try {
    Using.resource(new FileOutputStream(filename)) { stream =>
      stream.write(file)
      filename
    }
  }

  override def download(filename: String): Try[Array[Byte]] = Try {
    Using.resource(new FileInputStream(filename)) { stream =>
      stream.readAllBytes()
    }
}

//  override def getMeta(file: Array[Byte], filename: String): Try[String] = Try {
//  }
}
