package data

import io.circe.Json

case class MultipartFileWithMeta(meta: Json, file: Array[Byte])
