package tagservice.main

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import tagservice.configuration.Configuration._
import tagservice.service.{Tag, TagService}

object Client extends App {
  val client = Thrift.newIface[TagService.FutureIface](DefaultServerAddress)

  Await.ready(client.createTag(Tag(-1, "Hello")) onSuccess { r =>
    println("Received response: " + r)
  } onFailure { e =>
    e.printStackTrace()
  })
}
