package db.repositories

import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie._
import doobie.implicits._
import io.circe.Json
import io.circe.parser._
import org.postgresql.util.PGobject

import scala.util.Try

class SongDBService {
  private val pgUrl = System.getenv("POSTGRES_DB_URL")
  private val pgUser = System.getenv("POSTGRES_USER")
  private val pgPassword = System.getenv("POSTGRES_PASSWORD")

  private val pgConfig = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = s"jdbc:postgresql://$pgUrl",
    user = pgUser,
    password = pgPassword,
    logHandler = None
  )

  def getSongMetaById(songId: String): Try[String] = Try {
    sql"SELECT meta_data FROM song_meta WHERE song_id=$songId"
      .query[String]
      .unique
      .transact(pgConfig)
      .unsafeRunSync()
  }

  def insertSongMeta(songId: String, songMeta: String): Try[Unit] = Try {
      val pgObj =  new PGobject()
      pgObj.setType("json")
      pgObj.setValue(parse(songMeta).getOrElse(Json.Null).noSpaces)
//      val json_meta = convertToJson()
      sql"""INSERT INTO song_meta(song_id, meta_data)
            VALUES($songId, $pgObj)
         """
        .update
        .run
        .transact(pgConfig)
        .unsafeRunSync()
  }

  def convertToJson(json: Json): PGobject = {
    val pgObject = new PGobject()
    pgObject.setType("json")
    pgObject.setValue(json.noSpaces)
    pgObject
  }


}
