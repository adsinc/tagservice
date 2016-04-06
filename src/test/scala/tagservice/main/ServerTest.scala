package tagservice.main

import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.{Await, Closable, Future}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import tagservice.service.TagService.FutureIface
import tagservice.service.{Record, Tag, TagService}

class ServerTest extends FlatSpec with BeforeAndAfterEach with Matchers {
  var server: ListeningServer = _
  var client: FutureIface = _

  override protected def beforeEach(): Unit = {
    server = Server.start()
    client = Thrift.newIface[TagService.FutureIface](":8080")
  }

  override protected def afterEach(): Unit = Closable.close(server)

  "Create record" should "return unique id for new records" in {
    testGenerate(generateRecords())
  }

  "Create tag" should "return unique id for new tags" in {
    testGenerate(generateTags())
  }

  it should "check arguments" in {
    val ids = generateRecords()
  }

  def testGenerate[T](ids: => Seq[Long]) = ids.length shouldBe ids.distinct.length

  def generate[T](gegFn: Int => Future[T]): Seq[Long] = Await.result(Future.collect(
    1 to 10000 map (i => client.createRecord(Record(-1, "record" + i)))
  ))

  def generateRecords(): Seq[Long] = generate(n => client.createRecord(Record(-1, "record" + n)))

  def generateTags(): Seq[Long] = generate(n => client.createTag(Tag(-1, "tag" + n)))
}
