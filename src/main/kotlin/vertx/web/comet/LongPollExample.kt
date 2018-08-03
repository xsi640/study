package vertx.comet

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

val map = ConcurrentHashMap<String, LinkedBlockingQueue<String>>()

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route("/longpoll").blockingHandler { rc ->
        val request = rc.request()
        val name = request.getParam("name")
        val response = rc.response()
        while (true) {
            map.putIfAbsent(name, LinkedBlockingQueue())
            if (response.closed())
                break

            val queue = map[name]
            if (queue != null) {
                val msg = queue.poll()
                if (msg != null) {
                    response.end("$msg<br />")
                    break
                }
            }
            Thread.sleep(1)
        }
    }
    router.route("/send").handler { rc ->
        val request = rc.request()
        val name = request.getParam("name")
        val msg = request.getParam("msg")
        map.putIfAbsent(name, LinkedBlockingQueue())
        for (queue in map.values) {
            queue.add("$name:$msg")
        }
        rc.response().end()
    }

    router.route("/*").handler(StaticHandler.create())
    vertx.createHttpServer().requestHandler(router::accept).listen(8080)
}
