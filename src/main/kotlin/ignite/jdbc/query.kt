package ignite.jdbc

import org.apache.ignite.IgniteJdbcThinDriver
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

val CRAETE_TABLE = "CREATE TABLE IF NOT EXISTS Person (\n" +
        "  id varchar,\n" +
        "  name varchar,\n" +
        "  age int, \n" +
        "  address varchar,\n" +
        "  PRIMARY KEY (id)\n" +
        ") "

val INSERT = "INSERT INTO Person(id, name, age, address) VALUES(?,?,?,?)"

val SELECT = "SELECT id, name, age, address FROM Person"

fun main(args: Array<String>) {
    var conn = getConn()
    conn.createStatement().execute(CRAETE_TABLE)

    var state = conn.prepareStatement(INSERT)
    state.setString(1, UUID.randomUUID().toString())
    state.setString(2, "suyang")
    state.setInt(3, 30)
    state.setString(4, "Beijing")
    println("insert:${state.executeUpdate()}")
    println()
    state = conn.prepareStatement(SELECT)
    val rs = state.executeQuery()
    while (rs.next()) {
        println("id:${rs.getString("id")}")
        println("name:${rs.getString("name")}")
        println("age:${rs.getInt("age")}")
        println("address:${rs.getString("address")}")
    }
}

fun getConn(): Connection {
    return DriverManager.getConnection("jdbc:ignite:thin://127.0.0.1:10800")
}