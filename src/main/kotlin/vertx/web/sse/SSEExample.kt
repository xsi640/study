package sse

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler

@Volatile
var connectionCounter = 0

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route("/sse").handler { rc ->
        val response = rc.response()
        response.isChunked = true
        response.putHeader("Content-Type", "text/event-stream")
        response.putHeader("Cache-Control", "no-cache")
        response.putHeader("Connection", "keep-alive")

        connectionCounter++

        val time = vertx.setPeriodic(2000) {
            response.write("event: my-custom-event\n")
            response.write("id: ${connectionCounter}\n")
            response.write("data: ${connectionCounter} say hello\n\n")
        }

        response.endHandler {
            connectionCounter--
            vertx.cancelTimer(time)
        }
    }
    router.route("/*").handler(StaticHandler.create())
    vertx.createHttpServer().requestHandler(router::accept).listen(8080)
}