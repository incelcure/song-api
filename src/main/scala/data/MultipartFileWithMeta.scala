package data

import io.circe.Json
import sttp.tapir.TapirFile

case class MultipartFileWithMeta(meta: Json, file: TapirFile)
