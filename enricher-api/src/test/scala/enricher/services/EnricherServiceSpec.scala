package enricher.services

import io.circe._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class EnricherServiceSpec extends AnyFunSuite with Matchers {
//  test("artist name should be Avenade"){
//    val enricher = new EnricherService
//    val artistNames: Option[List[String]] = for {
//      artistsJson <-  enricher
//        .getMeta("64xpre2xJX11xbKq4wWdNH") // Avenade - Just smile and wave boys
//        .hcursor
//        .downField("artists").as[List[Json]].toOption
//      names = artistsJson.flatMap(_.hcursor.downField("name").as[String].toOption)
//    } yield names
//
//    artistNames.get.head shouldBe "Avenade"
//  }
}