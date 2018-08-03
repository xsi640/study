package vertx.web.ws

import io.vertx.core.*
import io.vertx.core.http.WebSocketBase
import java.util.concurrent.ConcurrentHashMap

val map = ConcurrentHashMap<String, WebSocketBase>()

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.createHttpServer().websocketHandler { ws ->
        if (ws.path() == "/wsapp") {
            map.putIfAbsent(ws.textHandlerID(), ws)

            ws.handler { buffer ->
                val msg = buffer.getString(0, buffer.length())
                println("receive message from ws-client " + ws.textHandlerID() + ": " + msg)
                map.values.forEach { w ->
                    w.writeTextMessage("${w.textHandlerID()}:$msg")
                }
            }

            ws.closeHandler { ch ->
                println("${ws.textHandlerID()} closed connection.")
                map.remove(ws.textHandlerID())
            }
        } else {
            ws.reject()
        }
    }.requestHandler { req ->
        if (req.path() == "/ws.html") {
            req.response().sendFile("webroot/ws.html")
        }
    }.listen(8080)

}