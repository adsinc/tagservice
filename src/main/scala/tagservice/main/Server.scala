package tagservice.main

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import tagservice.service.TagServiceHandler

object Server extends App {
  val server = Thrift.serveIface("localhost:8080", new TagServiceHandler)
  Await.ready(server)
}
