import io.circe.Json
import io.circe.parser.parse

import scala.util.Try
import scala.Predef
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._


class Enricher {
  private val back: WebSocketSyncBackend = HttpClientSyncBackend()
  private val clientId = System.getenv("CLIENT_ID")
  private val clientSecret = System.getenv("CLIENT_SECRET")
  private val tokenRequest: Request[Either[String, String]] = basicRequest
    .post(uri"https://accounts.spotify.com/api/token")
    .auth
    .basic(clientId, clientSecret)
    .body(Map("grant_type" -> "client_credentials"))


  private val clientCredentials = tokenRequest.response(asStringAlways).send(back).body
  private val json = parse(clientCredentials).getOrElse(Json.Null)
  private val accessToken = json.hcursor.downField("access_token").as[String].getOrElse("")

  def getMeta(songId: String): Json = {
    val metaSongRequest = basicRequest
      .get(uri"https://api.spotify.com/v1/tracks/$songId")
      .auth
      .bearer(accessToken)
      .response(asStringAlways)
      .send(back)
      .body

    parse(metaSongRequest).getOrElse(Json.Null)
  }
}

