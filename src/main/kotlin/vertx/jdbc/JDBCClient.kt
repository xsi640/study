package vertx.jdbc

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient


fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val config = JsonObject()
    config.put("url", "jdbc:ignite:thin://127.0.0.1:10800")
    val client = JDBCClient.createShared(vertx, config)
    client.getConnection {
        if (it.succeeded()) {
            var conn = it.result()
            conn.query("SELECT * FROM Person") { rs ->
                if (rs.succeeded()) {
                    var r = rs.result()
                    println(r.toJson())
                }
            }
        }
    }
}