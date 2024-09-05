import sttp.client4.{Request, UriContext, WebSocketSyncBackend, asStringAlways, basicRequest, multipart}
import sttp.client4.httpclient.HttpClientSyncBackend

import java.util.Base64

trait AuthCheck {
  def checkCreds(rawCreds: String): Boolean = {
    val encodedCredentials = Base64.getDecoder.decode(rawCreds.split(" ").last).map(_.toChar).mkString
    val creds = encodedCredentials.split(":")

    val back: WebSocketSyncBackend = HttpClientSyncBackend()

    val tokenRequest: Request[Either[String, String]] = basicRequest
      .get(uri"http://127.0.0.1:8081/login")
      .multipartBody(multipart("name", creds(0)), multipart("password", creds(1)))

    tokenRequest.response(asStringAlways)
      .send(back)
      .body
      .toBoolean
  }
}
