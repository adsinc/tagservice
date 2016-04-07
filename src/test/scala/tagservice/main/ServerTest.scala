package tagservice.main

import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.{Await, Closable, Future}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import tagservice.configuration.Configuration.DefaultServerAddress
import tagservice.service.TagService.FutureIface
import tagservice.service.{Record, Tag, TagService, TagServiceException}

class ServerTest extends FlatSpec with BeforeAndAfterEach with Matchers {
  var server: ListeningServer = _
  var client: FutureIface = _

  override protected def beforeEach(): Unit = {
    server = Server.start(DefaultServerAddress)
    client = Thrift.newIface[TagService.FutureIface](DefaultServerAddress)
  }

  override protected def afterEach(): Unit = {
    Await.result(Closable.close(server))
  }

  "Method createRecord" should "return unique id for new records" in {
    testGenerate(generateRecords())
  }

  "Method createTag" should "return unique id for new tags" in {
    testGenerate(generateTags())
  }

  it should "throw exception if tag with equals name exists" in {
    val tag = Tag(-1, "foo")
    Await.result(client.createTag(tag))
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.createTag(tag))
    }
  }

  "Method addTag" should "add tag to record" in {
    val Seq(recordId) = generateRecords(1)
    val tagIds = generateTags(10)
    val ids = addTagsToRecordAndGet(recordId, tagIds).map(_.id)
    ids.sorted shouldBe tagIds.sorted
  }

  it should "throw exception if tag or record not exists" in {
    testExceptionWithFakeId(client.addTag)
  }

  "Method deleteTag" should "delete tag from record" in {
    val Seq(recordId) = generateRecords(1)
    val tagIds = generateTags(10)
    val tags = addTagsToRecordAndGet(recordId, tagIds)
    val tagsAfterDel = Await.result(client.deleteTag(recordId, tags.head.id) flatMap { _ =>
      client.getTags(recordId)
    })
    tagsAfterDel.sortBy(_.id) shouldBe tags.tail.sortBy(_.id)
  }

  it should "throw exception if tag or record not exists" in {
    testExceptionWithFakeId(client.deleteTag)
  }

  it should "throw exception if tag not assigned to record" in {
    val Seq(recordId) = generateRecords(1)
    val Seq(tagId) = generateTags(1)
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.deleteTag(recordId, tagId))
    }
  }

  "Method getTags" should "get tags assigned to record" in {
    val Seq(recordId) = generateRecords(1)
    val tagIds = generateTags(10)
    val addedTags = addTagsToRecordAndGet(recordId, tagIds)
    Await.result(client.getTags(recordId)).sortBy(_.id) shouldBe addedTags.sortBy(_.id)
  }

  it should "throw exception if record not exists" in {
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.getTags(-1))
    }
  }

  "Method getRecords" should "get records by tags" in {
    def getRecords(tagIds: Seq[Long]) =
      Await.result(client.getRecords(tagIds take 2)).map(_.id)

    val Seq(recId1, recId2) = generateRecords(2)
    val tagIds = generateTags(5)
    addTagsToRecordAndGet(recId1, tagIds take 3)
    addTagsToRecordAndGet(recId2, tagIds drop 2)
    getRecords(tagIds take 2) shouldBe Seq(recId1)
    getRecords(tagIds drop 3) shouldBe Seq(recId2)
    getRecords(tagIds.slice(2, 3)).sorted shouldBe Seq(recId1, recId2).sorted
  }

  it should "throw exception if some tag not exists" in {
    val tagIds = generateTags(10)
    val Seq(recordId) = generateRecords(1)
    addTagsToRecordAndGet(recordId, tagIds)
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.getRecords(tagIds :+ (-1L)))
    }
    a[TagServiceException] shouldBe thrownBy {
      Await.result(client.getRecords(Seq(-1L)))
    }
  }

  def addTagsToRecordAndGet(recordId: Long, tagIds: Seq[Long]) = Await.result {
    Future.collect(tagIds map (client.addTag(recordId, _))) flatMap { _ =>
      client.getTags(recordId)
    }
  }

  def testExceptionWithFakeId(fn: (Long, Long) => Future[Unit]): Unit = {
    val Seq(existTag) = generateTags(1)
    val Seq(existRecord) = generateRecords(1)
    val fakeId = -1L
    a[TagServiceException] shouldBe thrownBy {
      Await.result(fn(fakeId, existTag))
    }
    a[TagServiceException] shouldBe thrownBy {
      Await.result(fn(existRecord, fakeId))
    }
    a[TagServiceException] shouldBe thrownBy {
      Await.result(fn(fakeId, fakeId))
    }
  }

  def testGenerate[T](ids: Seq[Long]) = ids.length shouldBe ids.distinct.length

  def generate[Long](gegFn: Int => Future[Long], count: Int = 100): Seq[Long] = Await.result(Future.collect(
    1 to count map gegFn
  ))

  def generateRecords(count: Int = 100): Seq[Long] =
    generate(n => client.createRecord(Record(-1, "record" + n)), count)

  def generateTags(count: Int = 100): Seq[Long] =
    generate(n => client.createTag(Tag(-1, "tag" + n)), count)
}
