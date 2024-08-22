import java.io.File
import scala.util.Try

trait FileService{
  def upload(file: Array[Byte], filename: String): Try[String]
  def download(filename: String): Try[Array[Byte]]
//  def getMeta(file: Array[Byte], filename: String): Try[String]
}
