package archery.server.http

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpContentCompressor

class ServerInitializer(router: PartialFunction[Route, ResponseWrapper])
	extends ChannelInitializer[SocketChannel] {

  override def initChannel(ch: SocketChannel): Unit = {
    val pipe = ch.pipeline
    pipe.addLast("codec", new HttpServerCodec)
    pipe.addLast("deflater", new HttpContentCompressor)
    pipe.addLast("handler", new RequestHandler(router))
  }
}