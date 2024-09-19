package db.repositories

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SongDBServiceSpec extends AnyFunSuite with Matchers {
  test("selected song meta should be {\"album\":\"scalaTest1\"}"){
    val pgTestService = new SongDBService
    val songTestId = "scalaTestSongId1"
    val songTestMeta = "{\"album\": \"scalaTest1\"}"
    pgTestService.insertSongMeta(songTestId, songTestMeta)
    pgTestService.getSongMetaById(songTestId).get shouldBe songTestMeta
  }
}