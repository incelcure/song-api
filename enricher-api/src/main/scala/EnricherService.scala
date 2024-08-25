import EnricherService.ClientCredentials
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

import scala.util.Try
import scala.Predef
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._


class EnricherService {
  private val back: WebSocketSyncBackend = HttpClientSyncBackend()
  private val clientId = System.getenv("CLIENT_ID")
  private val clientSecret = System.getenv("CLIENT_SECRET")
  private val tokenRequest: Request[Either[String, String]] = basicRequest
    .post(uri"https://accounts.spotify.com/api/token")
    .auth
    .basic(clientId, clientSecret)
    .body(Map("grant_type" -> "client_credentials"))


  private val clientCredentialsJson = tokenRequest.response(asStringAlways).send(back).body
  private val clientCredentials = decode[ClientCredentials](clientCredentialsJson)
    .getOrElse(throw new RuntimeException("Cant parse creds skibidi"))


  def getMeta(songId: String): Json = {
    val metaSongRequest = basicRequest
      .get(uri"https://api.spotify.com/v1/tracks/$songId")
      .auth
      .bearer(clientCredentials.access_token)
      .response(asStringAlways)
      .send(back)
      .body

    parse(metaSongRequest).getOrElse(Json.Null)
  }
}

object EnricherService {
  private case class ClientCredentials(access_token: String)

  private implicit val clientCredentialsDecoder: Decoder[ClientCredentials] = deriveDecoder

}