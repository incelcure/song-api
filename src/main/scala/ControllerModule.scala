import scala.concurrent.ExecutionContext

class ControllerModule(serviceModule: ServiceModule)(implicit ex : ExecutionContext) {
  val authEndpointBuilder = new AuthEndpointBuilder(???)

  val fileController = new FileController(serviceModule.s3FileService, authEndpointBuilder)
}
