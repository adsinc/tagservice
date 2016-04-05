package tagservice.service

import com.twitter.util.Future

import scala.collection.mutable

class TagServiceHandler extends TagService.FutureIface {
  val tags = mutable.Map[Long, Tag]()
  val records = mutable.Map[Long, Record]()
  val recordsToTags = mutable.Map[Long, Set[Long]]()

  override def addTag(record: Record, tag: Tag): Future[Unit] = Future {
    //todo check args ???
    val tagId = tag.id
    val recId = record.id
    if (!records.contains(recId) && tags.contains(tagId)) {
      //todo validation
      ???
    }
    val currentTags = recordsToTags(recId)
    if(currentTags.contains(tagId)) {
      ???
    }
    recordsToTags += recId -> (currentTags + tag.id)
  }

  override def getRecords(tags: Seq[Tag]): Future[Seq[Record]] = Future {
    val tagIds = tags.map(_.id).toSet
    (for {
      (recId, regTagIds) <- recordsToTags
      tagId <- regTagIds
      if tagIds.contains(tagId)
    } yield records(recId)).toSeq
  }

  override def deleteTag(record: Record, tag: Tag): Future[Unit] = Future {
    recordsToTags += record.id -> (recordsToTags(record.id) - tag.id)
  }

  override def getTags(record: Record): Future[Seq[Tag]] = Future {
    val tagIds = recordsToTags(record.id)
    tags.filterKeys(tagIds.contains).values.toSeq
  }
}
