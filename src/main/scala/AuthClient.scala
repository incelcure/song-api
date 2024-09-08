import data.Credentials
import sttp.client4.{Request, UriContext, WebSocketSyncBackend, asStringAlways, basicRequest, multipart}
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema}
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser._
import io.circe.syntax.EncoderOps

import java.util.Base64
import scala.concurrent.Future


// signature should look like this
trait AuthClient {
  def authenticate(credentials : Credentials) : Future[Boolean]
}


class AuthClientImpl(httpClient: WebSocketSyncBackend) extends AuthClient {
  def authenticate(rawCreds: String): Boolean = {
    // todo: maybe check for valid creds?

    val encodedCredentials = Base64.getDecoder
      .decode(rawCreds.split(" ").last)
      .map(_.toChar).mkString

    val Array(login, password) = encodedCredentials.split(":")


    // todo: rewrite this... send json
    val tokenRequest: Request[Either[String, String]] = basicRequest
      .get(uri"http://127.0.0.1:8081/login")
      .multipartBody(multipart("name", creds(0)), multipart("password", creds(1)))

    tokenRequest.response(asStringAlways)
      .send(httpClient)
      .body
      .toBoolean
  }
}