package archery.server.http

import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import java.util.logging.Logger
import java.util.logging.Level
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.channel.ChannelFutureListener

import RequestHandler._

class RequestHandler(router: PartialFunction[Route, ResponseWrapper]) 
	extends ChannelInboundHandlerAdapter {
  
  private val logger : Logger = Logger.getLogger(
            classOf[RequestHandler].getName());
  
  override def channelRead(ctx: ChannelHandlerContext, msg: Object) = {
    msg match {
      case req:HttpRequest => {
        val route = parseRoute(req.getMethod.name, req.getUri)
        val response = router(route)
        val httpResponse = new DefaultFullHttpResponse(HTTP_1_1, response.status, Unpooled.wrappedBuffer(response.content.getBytes("utf-8")))
        httpResponse.headers().set(CONTENT_TYPE, response.contentType)
        httpResponse.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes())
        HttpHeaders.isKeepAlive(req) match {
          case true => {
            ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE)
          }
          case false => {
            httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
            ctx.write(httpResponse)
          }
        }
      }
      case _ => Nil
    }    
  }
  
  override def channelReadComplete(ctx: ChannelHandlerContext) = ctx.flush
  
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = {
    logger.log(Level.WARNING, "Unexpected exception from downstream.", cause)
    ctx.close()
  }
}

/**
 * route/path parsers
 */
object RequestHandler {

  private def parsePath(pathStr: String) =
    pathStr.trim.split('/').filter(_.length > 0)

  def parseRoute(method: String, pathStr: String): Route =
    Route(method, parsePath(pathStr):_*)
}