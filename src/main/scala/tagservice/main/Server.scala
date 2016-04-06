package tagservice.main

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import tagservice.service.TagServiceHandler

object Server extends App {
  val server = start()
  Await.ready(server)

  def start() = Thrift.serveIface(":8080", new TagServiceHandler)
}
