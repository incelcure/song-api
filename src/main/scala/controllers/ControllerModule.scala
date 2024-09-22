package controllers

import auth.AuthEndpointBuilder
import com.amazonaws.services.connectcases.model.FileContent
import services.ServiceModule

import scala.concurrent.ExecutionContext

class ControllerModule(serviceModule: ServiceModule)(implicit ex: ExecutionContext) {
  val authEndpointBuilder = new AuthEndpointBuilder(serviceModule.authClient)

  val fileController =
    new FileController(serviceModule.s3FileService, authEndpointBuilder)(serviceModule.enricherService)

  val endpoints = fileController.endpoints
}
