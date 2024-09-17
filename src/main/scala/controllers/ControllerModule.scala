package controllers

import auth.AuthEndpointBuilder
import services.ServiceModule

import scala.concurrent.ExecutionContext

class ControllerModule(serviceModule: ServiceModule)(implicit ex: ExecutionContext) {
  val authEndpointBuilder = new AuthEndpointBuilder(serviceModule.authClient)

  val fileController =
    new FileController(serviceModule.s3FileService, authEndpointBuilder)(serviceModule.enricherService)

}
