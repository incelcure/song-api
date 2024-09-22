package auth

import data.Credentials
import sttp.model.{HeaderNames, StatusCode}
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{endpoint, header, statusCode}

import java.util.Base64
import scala.concurrent.{ExecutionContext, Future}


class AuthEndpointBuilder(authClient: AuthClient)(implicit ex: ExecutionContext) {
  val authEndpoint: PartialServerEndpoint[String, Credentials, Unit, StatusCode, Unit, Any, Future] = endpoint
    .securityIn(header[String](HeaderNames.Authorization))
    .errorOut(statusCode)
    .serverSecurityLogic { basic =>
      val encodedCredentials = Base64.getDecoder
        .decode(basic.split(" ").last)
        .map(_.toChar).mkString

      val Array(login, password) = encodedCredentials.split(":")
      val creds: Credentials = new Credentials(login, password)

      authClient.authenticate(creds)
        .map(isAuthenticated => {
          Either.cond(isAuthenticated, creds, StatusCode.Unauthorized)
        })
    }
}