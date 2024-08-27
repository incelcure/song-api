import doobie._
import doobie.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie.postgres.circe.jsonb.implicits._
import io.circe.Json
import io.circe.parser._

object Main {
  def main(args: Array[String]): Unit = {
    println("Hey, Song Api")

    val pgUrl = System.getenv("POSTGRES_DB_URL")
    val pgUser = System.getenv("POSTGRES_USER")
    val pgPassword = System.getenv("POSGRES_PASSWORD")

    val xa = Transactor.fromDriverManager[IO](
      driver = "org.postgresql.Driver",
      url = s"jdbc:postgresql://$pgUrl",
      user = pgUser,
      password = pgPassword,
      logHandler = None
    )
    val metaRequest = sql"select meta_data from song_meta"
      .query[String]
      .to[List]

    val ioMetaRequest = metaRequest.transact(xa)

    def insertMeta(songId: String, songMeta: String): Update0 = {
      val json_meta = parse(songMeta).getOrElse(Json.Null)
      sql"""INSERT INTO song_meta(song_id, meta_data)
            VALUES($songId, $json_meta)
         """.update
    }

    insertMeta("songIdInsertTest2", "{\"album\":\"testAlbum\", \"artist\":\"testArtist2\"}")
      .run
      .transact(xa)
      .unsafeRunSync()



    println(ioMetaRequest.unsafeRunSync())
  }
}