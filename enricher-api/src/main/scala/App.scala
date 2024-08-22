import sttp.client4.basicRequest
import sttp.client4.httpclient.HttpClientSyncBackend
import sttp.client4._


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

    val tokenResponse = tokenRequest.response(asStringAlways).send(back)
    val access_token = tokenResponse.body
    println(access_token)
  }
}