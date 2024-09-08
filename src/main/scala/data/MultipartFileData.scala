package data

case class MultipartFileData(filename: String, file: Array[Byte])

  // todo: write without auto derivation codec


//  implicit val multipartFileDataCodec : MultipartCodec[MultipartFileData] =  MultipartCodec.Default.mapDecode { parts =>
//    for {
//     filenamePart <- parts.find(_.name == "filename")
//     filePart <- parts.find(_.name == "file")
//    } yield MultipartFileData(new String(filenamePart.body, "UTF-8"), filePart.body)
//  }()

