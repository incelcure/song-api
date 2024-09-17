import data.Credentials
import sttp.client4.{Request, UriContext, WebSocketSyncBackend, asStringAlways, basicRequest, multipart}
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema}
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser._
import io.circe.syntax.EncoderOps

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}


trait AuthClient {
  def authenticate(credentials : Credentials) : Future[Boolean]
}

class AuthClientImpl(httpClient: WebSocketSyncBackend)(implicit ex : ExecutionContext) extends AuthClient{
  override def authenticate(credentials: Credentials): Future[Boolean] = Future {
    val tokenRequest: Request[Either[String, String]] = basicRequest
      .get(uri"http://127.0.0.1:8081/login")
      .body(Map("name"->credentials.name, "password" -> credentials.password))

    tokenRequest.response(asStringAlways)
      .send(httpClient)
      .body
      .toBoolean
  }
}