package archery.server.http

import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus._

class ResponseWrapper(val content: String = "", 
    				  val status:HttpResponseStatus = OK, 
    				  val contentType:String = "text/plain") { }