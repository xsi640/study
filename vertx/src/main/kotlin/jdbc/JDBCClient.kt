package vertx.jdbc

import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import org.apache.ignite.IgniteJdbcThinDriver
import org.apache.ignite.internal.jdbc.thin.JdbcThinConnection
import org.apache.ignite.internal.jdbc.thin.JdbcThinUtils
import org.apache.ignite.internal.util.typedef.F
import java.sql.*
import java.util.*
import java.util.logging.Logger


fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val config = JsonObject()
    config.put("url", "jdbc:ignite:custom://127.0.0.1:10800")
    val client = JDBCClient.createShared(vertx, config)
    client.getConnection {
        if (it.succeeded()) {
            var conn = it.result()
            conn.updateWithParams("INSERT INTO Person(id, name, age, address) VALUES(?,?,?,?)",
                    JsonArray().add(UUID.randomUUID().toString())
                            .add("zhangsan")
                            .add(18)
                            .add("Shanghai")) { rs ->
                if (rs.failed()) {
                    println(rs.cause())
                } else {
                    conn.query("SELECT * FROM Person") { rs ->
                        if (rs.succeeded()) {
                            var r = rs.result()
                            println(r.toJson())
                        }
                    }
                }
            }
        }
    }
}