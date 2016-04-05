package tagservice.service

import com.twitter.util.Future

class TagServiceHandler extends TagService[Future] {
  override def addTag(record: Record, tag: Tag): Future[Unit] = ???

  override def getRecords(tags: Seq[Tag]): Future[Seq[Record]] = ???

  override def deleteTag(record: Record, tag: Tag): Future[Unit] = ???

  override def getTags(record: Record): Future[Seq[Tag]] = Future(Seq(Tag(1, "helloTag")))
}
