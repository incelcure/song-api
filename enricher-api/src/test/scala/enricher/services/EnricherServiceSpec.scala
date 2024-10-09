package enricher.services

import io.circe._
import io.circe.parser._
import io.circe.JsonObject
import io.circe.syntax.EncoderOps
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import sttp.client4.WebSocketSyncBackend
import sttp.client4.httpclient.HttpClientSyncBackend

class EnricherServiceSpec extends AnyFunSuite with Matchers {
  val back: WebSocketSyncBackend = HttpClientSyncBackend()
  val enricher = new EnricherService(back)

  test("artist name should be Avenade"){
    val artistNames: Option[List[String]] =
      for {
        artistsJson <- enricher.getMeta("64xpre2xJX11xbKq4wWdNH").get.hcursor.downField("artists").as[List[Json]].toOption
        names = artistsJson.flatMap(_.hcursor.downField("name").as[String].toOption)
      } yield names

    artistNames.get.head shouldBe "Avenade"
  }
}