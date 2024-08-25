import sttp.client4.basicRequest
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._
import io.circe._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser._

object Main {
  def main(args: Array[String]): Unit = {
    println("Hey, enricher API")

    val enricher = new EnricherService
    println(enricher.getMeta("64xpre2xJX11xbKq4wWdNH")) // Avenade - Just smile and wave boys


  }
}