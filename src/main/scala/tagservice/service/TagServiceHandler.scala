package tagservice.service

import com.twitter.util.Future
import tagservice.database.DataBase

import scala.collection._

class TagServiceHandler(val dataBase: DataBase) extends TagService.FutureIface {
  override def addTag(recordId: Long, tagId: Long): Future[Unit] =
    dataBase.addTagToRecord(recordId, tagId)

  override def deleteTag(recordId: Long, tagId: Long): Future[Unit] =
    dataBase.deleteTagFromRecord(recordId, tagId)

  override def getTags(recordId: Long): Future[Seq[Tag]] =
    dataBase.getTagsForRecord(recordId)

  override def getRecords(tagIds: Seq[Long]): Future[Seq[Record]] =
    dataBase.getRecordsForTags(tagIds)

  override def createTag(tag: Tag): Future[Long] =
    dataBase.createTag(tag)

  override def createRecord(record: Record): Future[Long] =
    dataBase.createRecord(record)
}
