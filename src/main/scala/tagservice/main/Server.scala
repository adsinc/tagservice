package tagservice.main

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import tagservice.configuration.Configuration._
import tagservice.database.DataBase
import tagservice.service.TagServiceHandler

object Server extends App {
  val server = start(DefaultServerAddress)
  Await.ready(server)

  def start(address: String) = Thrift.serveIface(address, new TagServiceHandler(DataBase()))
}
