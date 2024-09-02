import doobie.Transactor
import doobie._
import doobie.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import io.circe.Json
import io.circe.parser._
import scala.util.Try

class AuthService {
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

  def register(name: String, password: String): Try[Unit] = Try {
    sql"""INSERT INTO user_table (id, name, password)
         VALUES (DEFAULT, $name, $password)
       """
      .update
      .run
      .transact(pgConfig)
      .unsafeRunSync()
  }

  def login(name: String, password: String): Try[Boolean] = Try {
    sql"""SELECT password FROM user_table
          WHERE name=$name
       """
      .query[String]
      .unique
      .transact(pgConfig)
      .unsafeRunSync() == password
  }
}
