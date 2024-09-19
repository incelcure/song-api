package file.services

import java.io.{File, FileInputStream, FileOutputStream}
import scala.util
import scala.util.{Try, Using}

class SystemFileService extends FileService {

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
}
