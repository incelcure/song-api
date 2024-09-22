import doobie.Transactor
import doobie._
import doobie.implicits._
import cats.effect._
import cats.effect.unsafe.implicits.global
import doobie.util.transactor.Transactor.Aux
import io.circe.Json
import io.circe.parser._

import scala.util.Try

class AuthService(pgConfig: Aux[IO, Unit]) {
  def register(name: String, password: String): Try[String] = Try {
    sql"""INSERT INTO user_table (name, password)
         VALUES ($name, $password)
       """
      .update
      .run
      .transact(pgConfig)
      .unsafeRunSync().toString //"Query response idk"
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
