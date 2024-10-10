import akka.actor.ActorSystem
import cats.effect.IO
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class AuthServiceSpec extends AnyFunSuite with Matchers{
  val pgUrl = System.getenv("POSTGRES_DB_URL")
  val pgUser = System.getenv("POSTGRES_USER")
  val pgPassword = System.getenv("POSTGRES_PASSWORD")

  val pgConfig: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = s"jdbc:postgresql://$pgUrl",
    user = pgUser,
    password = pgPassword,
    logHandler = None
  )
  val authService = new AuthService(pgConfig)

  test("register query should return \"1\"") {
    authService.register("test111", "test111Pass").get shouldBe "1"
  }

  test("login query should return true") {
    authService.login("test1", "test1pass").get shouldBe true
  }

  test("login should return true after register"){
    authService.register("test112", "test112pass")
    authService.login("test112", "test112pass").get shouldBe true
  }

  test("login should return false after register"){
    authService.register("test113", "test113pass")
    authService.login("test113", "test12pass").get shouldBe false
  }
}
