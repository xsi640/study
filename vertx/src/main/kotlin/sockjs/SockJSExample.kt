package sockjs

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import java.util.concurrent.ConcurrentHashMap

val map = ConcurrentHashMap<String, SockJSSocket>()

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    val options = SockJSHandlerOptions()
            .setHeartbeatInterval(2000)
    val sockJSHandler = SockJSHandler.create(vertx, options)
    sockJSHandler.socketHandler { sockJSSocket ->
        map.putIfAbsent(sockJSSocket.writeHandlerID(), sockJSSocket)

        sockJSSocket.handler { buffer ->
            val msg = buffer.getString(0, buffer.length())
            println("receive message from ws-client " + sockJSSocket.writeHandlerID() + ": " + msg)
            map.values.forEach { s ->
                s.write("${s.writeHandlerID()}:$msg")
            }
        }

        sockJSSocket.endHandler {
            println("${sockJSSocket.writeHandlerID()} closed connection.")
            map.remove(sockJSSocket.writeHandlerID())
        }
    }
    router.route("/sockjs/*").handler(sockJSHandler)
    router.route("/*").handler(StaticHandler.create())
    vertx.createHttpServer().requestHandler(router::accept).listen(8080)

}