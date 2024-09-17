package data

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}
import sttp.tapir.{Codec, CodecFormat, DecodeResult, Schema}

case class Credentials(name: String, password: String)

object Credentials {
  implicit val credentialsDecoder: Decoder[Credentials] = deriveDecoder
  implicit val credentialsEncoder: Encoder[Credentials] = deriveEncoder
  implicit val credentialsSchema: Schema[Credentials] = Schema.derived

  implicit val credentialsCodec: Codec[String, Credentials, CodecFormat.Json] = Codec.json(str =>
    decode[Credentials](str) match {
      case Left(err) => DecodeResult.Error(str, err)
      case Right(value) => DecodeResult.Value(value)
    }
  )(_.asJson.noSpaces)
}