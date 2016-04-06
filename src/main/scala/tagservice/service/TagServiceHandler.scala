package tagservice.service

import java.util.concurrent.atomic.AtomicLong

import com.twitter.util.Future

import scala.collection._
import scala.collection.concurrent.TrieMap

class TagServiceHandler extends TagService.FutureIface {
  val tags = TrieMap[Long, Tag]()
  val records = TrieMap[Long, Record]()
  val recordsToTags = TrieMap[Long, Set[Long]]()
  val idGen = new AtomicLong()

  override def addTag(recordId: Long, tagId: Long): Future[Unit] = Future {
    if (!records.contains(recordId) && tags.contains(tagId)) {
      ???
    }
    val currentTags = recordsToTags(recordId)
    if(currentTags.contains(tagId)) {
      ???
    }
    recordsToTags(recordId) = currentTags + tagId
  }

  override def deleteTag(recordId: Long, tagId: Long): Future[Unit] = Future {
    recordsToTags += recordId -> (recordsToTags(recordId) - tagId)
  }

  override def getTags(recordId: Long): Future[Seq[Tag]] = Future {
    val tagIds = recordsToTags(recordId)
    tags.filterKeys(tagIds.contains).values.toSeq
  }

  override def getRecords(tagIds: Seq[Long]): Future[Seq[Record]] = Future {
    {for {
      (recId, regTagIds) <- recordsToTags
      tagId <- regTagIds
      if tagIds.contains(tagId)
    } yield records(recId)}.toSeq
  }

  override def createTag(tag: Tag): Future[Long] = Future {
    //todo
    val id = idGen.incrementAndGet()
    tags(id) = tag
    id
  }

  override def createRecord(record: Record): Future[Long] = Future {
    //todo
    val id = idGen.incrementAndGet()
    records(id) = record
    id
  }
}
