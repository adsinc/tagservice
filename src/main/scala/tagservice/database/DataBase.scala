package tagservice.database

import java.util.concurrent.atomic.AtomicLong

import com.twitter.util.Future
import tagservice.service.{Record, Tag, TagServiceException}

import scala.collection.mutable
import scala.collection.concurrent.TrieMap

class DataBase {
  private val tags = TrieMap[Long, Tag]()
  private val records = TrieMap[Long, Record]()
  private val recordsToTags = TrieMap[Long, Set[Long]]()
  private val idGen = new AtomicLong()

  def getRecordById(recordId: Long): Future[Record] = records.get(recordId) match {
    case None => Future.exception(TagServiceException("No such record"))
    case Some(record) => Future(record)
  }

  def getTagById(tagId: Long): Future[Tag] = tags.get(tagId) match {
    case None => Future.exception(TagServiceException("No such tag"))
    case Some(record) => Future(record)
  }

  def addTagToRecord(recordId: Long, tagId: Long) = {
    getRecordById(recordId) join getTagById(tagId) flatMap { _ =>
      val tagIds = getRecordTags(recordId)
      if(tagIds(tagId))
        Future.exception(TagServiceException("Tag already added"))
      else {
        recordsToTags(recordId) = tagIds + tagId
        Future.Unit
      }
    }
  }

  def deleteTagFromRecord(recordId: Long, tagId: Long): Future[Unit] = {
    getRecordById(recordId) join getTagById(tagId) flatMap { _ =>
      val tagIds = getRecordTags(recordId)
      if(!tagIds(tagId))
        Future.exception(TagServiceException("Tag not added"))
      else {
        recordsToTags(recordId) = tagIds - tagId
        Future.Unit
      }
    }
  }

  private def getRecordTags(recordId: Long): Set[Long] = {
    recordsToTags.getOrElse(recordId, Set())
  }

  def getTagsForRecord(recordId: Long): Future[Seq[Tag]] = {
    getRecordById(recordId) flatMap { _ =>
      Future {
        val tagIds = getRecordTags(recordId)
        tags.filterKeys(tagIds).values.toSeq
      }
    }
  }

  def getRecordsForTags(tagIds: Seq[Long]): Future[Seq[Record]] = {
    Future.join(tagIds.map(getTagById)) flatMap { _ =>
      Future {
        (for {
          (recId, regTagIds) <- recordsToTags
          tagId <- regTagIds
          if tagIds.contains(tagId)
        } yield records(recId)).toSeq
      }
    }
  }

  private def createData[T](data: T, table: mutable.Map[Long, T]): Future[Long] = Future.value {
    val id = idGen.incrementAndGet()
    table(id) = data
    id
  }

  def createRecord(record: Record): Future[Long] = createData(record, records)

  def createTag(tag: Tag): Future[Long] = {
    val trimmedName = tag.name.trim
    if(tags.values.exists(_.name == trimmedName))
      Future.exception(TagServiceException(s"Tag with name ${tag.name} already exists"))
    else {
      createData(tag.copy(name = trimmedName), tags)
    }
  }
}