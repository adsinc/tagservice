package client

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import tagservice.service.{Record, TagService, TagServiceHandler}

object Client extends App {
  println("HELLO")
  val client = Thrift.newIface[TagService.FutureIface]("localhost:8080")
  val future = client.getTags(Record(1, "hello")) onSuccess { r =>
    println("Received response: " + r)
  } onFailure { e =>
    e.printStackTrace()
  }
  Await.ready(future)
}