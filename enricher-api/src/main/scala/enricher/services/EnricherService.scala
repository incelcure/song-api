package enricher.services

import EnricherService.ClientCredentials
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

import scala.util.{Success, Try}
import scala.Predef
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._


class EnricherService(backend: WebSocketSyncBackend) {
  private val clientId = System.getenv("CLIENT_ID")
  private val clientSecret = System.getenv("CLIENT_SECRET")

  def getMeta(songId: String): Try[Json] = {
    val clientCredentialsJson: String = {
      val tokenRequest: Request[Either[String, String]] = basicRequest
        .post(uri"https://accounts.spotify.com/api/token")
        .auth
        .basic(clientId, clientSecret)
        .body(Map("grant_type" -> "client_credentials"))

      tokenRequest.response(asStringAlways).send(backend).body
    }

    val clientCredentials = decode[ClientCredentials](clientCredentialsJson)
      .getOrElse(throw new RuntimeException("Cant parse creds skibidi"))

    val metaSongRequest = basicRequest
      .get(uri"https://api.spotify.com/v1/tracks/$songId")
      .auth
      .bearer(clientCredentials.access_token)
      .response(asStringAlways)
      .send(backend)
      .body

    Success(parse(metaSongRequest).getOrElse(Json.Null))
  }
}

object EnricherService {
  private case class ClientCredentials(access_token: String)

  private implicit val clientCredentialsDecoder: Decoder[ClientCredentials] = deriveDecoder

}