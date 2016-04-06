package tagservice.main

import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.Closable
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import tagservice.service.TagService
import tagservice.service.TagService.FutureIface

class ServerTest extends FlatSpec with BeforeAndAfterEach with Matchers {
  var server: ListeningServer = _
  var client: FutureIface = _

  override protected def beforeEach(): Unit = {
    server = Server.start()
    client = Thrift.newIface[TagService.FutureIface](":8080")
  }

  override protected def afterEach(): Unit = Closable.close(server)


}
