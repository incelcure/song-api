import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

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
    println(ioMetaRequest.unsafeRunSync())

  }
}