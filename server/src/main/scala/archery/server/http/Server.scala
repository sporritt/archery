package archery.server.http

import java.net.InetSocketAddress
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.channel.ChannelOption
import archery.server.Forest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus._
import archery.server.JacksonWrapper
import archery.Box

/**
 * Http server wrapper around a Forest.
 */
class Server[A](val host:String = "localhost", val port:Int = 8008, valueConverter: String => A) {
  
  protected lazy val forest:Forest[A] = { new Forest[A]() }
  
  protected lazy val bossGroup: EventLoopGroup = { new NioEventLoopGroup() }
  protected lazy val workerGroup: EventLoopGroup = { new NioEventLoopGroup() }
  
  def reply(content:String, status:HttpResponseStatus = OK, contentType:String) = {
	  new ResponseWrapper(content, status, contentType)
  }
  
  def respond(obj:Any, status:HttpResponseStatus = OK, contentType:String) = {
    new ResponseWrapper(JacksonWrapper.serialize(obj), status, contentType)
  }  
  
  val router: PartialFunction[Route, ResponseWrapper] = {
	case Route("GET", "ping") => reply("PONG\n", OK, "text/plain")
	case Route("GET", "ping", c) => reply("PONG with " + c + "\n", OK, "text/plain")
	
	case Route("GET", "list") => respond(forest.list, OK, ContentTypes.JSON)
	
	case Route("GET", "get", treeId) => respond(forest.get(treeId), OK, ContentTypes.JSON)
	
	case Route("PUT", "add") => respond(forest.add(), OK, ContentTypes.JSON)
	case Route("PUT", "add", desc) => respond(forest.add(desc), OK, ContentTypes.JSON)
	
	case Route("PUT", "insert", treeId:String, x, y, value) => {
		val ok = forest.insert(treeId, x.toFloat, y.toFloat, valueConverter(value))
		respond(Responses.OK, OK, ContentTypes.JSON)
    }
	
	case Route("GET", "search", treeId, x, y, w, h) => {
	  val b = Box(x.toFloat, y.toFloat, w.toFloat, h.toFloat)
	  respond(forest.search(treeId, b), OK, ContentTypes.JSON)
	}
	
	case Route("POST", "remove", treeId) => {
	  respond(forest.remove(treeId), OK, ContentTypes.JSON)
	}
	
	case Route("POST", "remove", treeId, entryId) => {
	  respond(forest.remove(treeId, entryId), OK, ContentTypes.JSON)
	}
  }
  

  protected lazy val bootstrap: ServerBootstrap = {
    new ServerBootstrap().
    group(bossGroup, workerGroup).
    channel(classOf[NioServerSocketChannel]).
    childHandler(new ServerInitializer(router orElse StaticRoutes.fail)).
    localAddress(new InetSocketAddress(host, port))
  }  
  
  def run: Unit = {
    try {
      val chan = bootstrap.bind().sync.channel
      chan.closeFuture.sync
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
  
  def shutdown: Unit = {
    bossGroup.shutdownGracefully();
	workerGroup.shutdownGracefully();
  }
}

object ContentTypes {
  val JSON = "application/json"
}

object Responses {
  val OK = ("success"-> true)
  val ERROR = ("success" -> false)
}

object Server {

	lazy val server  = new Server[String]("localhost", 8008, (s:String) => s)
	
	def run: Unit = {
		println("starting ...")
		server.run
		println("exiting ...")
	}

	def shutdown: Unit = {
		server.shutdown
	}
  
	def main(args: Array[String]) {
		server.run
	}
}

object StaticRoutes {

  import HttpResponseStatus._

  val fail: PartialFunction[Route, ResponseWrapper] = {
    case _ => new ResponseWrapper(status=BAD_REQUEST)
  }

}