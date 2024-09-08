import data.Credentials
import sttp.model.{HeaderNames, StatusCode}
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{endpoint, header, statusCode}

import scala.concurrent.{ExecutionContext, Future}


class AuthEndpointBuilder(authClient: AuthClient)(implicit ex : ExecutionContext) {
  val authEndpoint: PartialServerEndpoint[String, Nothing, Unit, StatusCode, Unit, Any, Future] = endpoint
      .securityIn(header[String](HeaderNames.Authorization))
      .errorOut(statusCode)
      .serverSecurityLogic { basic =>
        // 1. parse basic
        val creds : Credentials = ???
        // 2. pass to authClient.authenticate


        authClient.authenticate(???)
          .map(isAuthenticated => Either.cond(isAuthenticated, StatusCode.Unauthorized, creds))
      }

// ADT,
}
