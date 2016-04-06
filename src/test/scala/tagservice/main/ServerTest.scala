package tagservice.main

import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.{Await, Closable, Future}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import tagservice.configuration.Configuration.DefaultServerAddress
import tagservice.service.TagService.FutureIface
import tagservice.service.{Record, Tag, TagService, TagServiceException}

class ServerTest extends FlatSpec with BeforeAndAfterAll with Matchers {
  var server: ListeningServer = _
  var client: FutureIface = _

  override protected def beforeAll(): Unit = {
    server = Server.start(DefaultServerAddress)
    client = Thrift.newIface[TagService.FutureIface](DefaultServerAddress)
  }

  override protected def afterAll(): Unit = Await.result(Closable.close(server))

  "Method createRecord" should "return unique id for new records" in {
    def generateRecords(): Seq[Long] =
      generate(n => client.createRecord(Record(-1, "record" + n)))

    testGenerate(generateRecords())
  }

  "Method createTag" should "return unique id for new tags" in {
    def generateTags(): Seq[Long] =
      generate(n => client.createTag(Tag(-1, "tag" + n)))

    testGenerate(generateTags())
  }

  it should "throw exception if tag with equals name exists" in {
    val tag = Tag(-1, "foo")
    Await.result(client.createTag(tag))
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.createTag(tag))
    }
  }

  "Method addTag" should "" in {
    //todo
  }

  "Method deleteTag" should "" in {
    //todo
  }

  "Method getTags" should "" in {
    //todo
  }

  "Method getRecords" should "" in {
    //todo
  }

  def testGenerate[T](ids: => Seq[Long]) = ids.length shouldBe ids.distinct.length

  def generate[T](gegFn: Int => Future[T]): Seq[Long] = Await.result(Future.collect(
    1 to 100 map (i => client.createRecord(Record(-1, "record" + i)))
  ))
}
