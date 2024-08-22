import sttp.client4.basicRequest
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._
import io.circe._
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.parser._

object Main {
  def main(args: Array[String]): Unit = {
    println("Hey, enricher API")


    val back = HttpClientSyncBackend()
    val clientId = System.getenv("CLIENT_ID")
    val clientSecret = System.getenv("CLIENT_SECRET")
    val tokenRequest = basicRequest
      .post(uri"https://accounts.spotify.com/api/token")
      .auth
      .basic(clientId, clientSecret)
      .body(Map("grant_type" -> "client_credentials"))


    val clientCredentials = tokenRequest.response(asStringAlways).send(back).body
    val json = parse(clientCredentials).getOrElse(Json.Null)
    val accessToken = json.hcursor.downField("access_token").as[String].getOrElse("")

    println(accessToken)

    val songId = "64xpre2xJX11xbKq4wWdNH" // Avenade - Just smile and wave boys

    val metaSongRequest = basicRequest
      .get(uri"https://api.spotify.com/v1/tracks/64xpre2xJX11xbKq4wWdNH")
      .auth
      .bearer(accessToken)
      .response(asStringAlways)
      .send(back)
      .body

    val songMeta = parse(metaSongRequest).getOrElse(Json.Null)
    println(songMeta)

  }
}