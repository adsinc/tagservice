package tagservice.main

import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.{Await, Closable, Future}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import tagservice.configuration.Configuration.DefaultServerAddress
import tagservice.service.TagService.FutureIface
import tagservice.service.{Record, Tag, TagService}

class ServerTest extends FlatSpec with BeforeAndAfterAll with Matchers {
  var server: ListeningServer = _
  var client: FutureIface = _

  override protected def beforeAll(): Unit = {
    server = Server.start(DefaultServerAddress)
    client = Thrift.newIface[TagService.FutureIface](DefaultServerAddress)
  }

  override protected def afterAll(): Unit = Closable.close(server)

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
    1 to 100 map (i => client.createRecord(Record(-1, "record" + i)))
  ))

  def generateRecords(): Seq[Long] = generate(n => client.createRecord(Record(-1, "record" + n)))

  def generateTags(): Seq[Long] = generate(n => client.createTag(Tag(-1, "tag" + n)))
}
