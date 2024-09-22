package data

import sttp.tapir.TapirFile

case class MultipartFileData(filename: String, file: TapirFile)

