package server

import com.twitter.finagle._
import com.twitter.util.Await
import tagservice.service.TagServiceHandler

object Server extends App {
  val server = Thrift.serveIface("localhost:8080", new TagServiceHandler)
  Await.ready(server)
}
