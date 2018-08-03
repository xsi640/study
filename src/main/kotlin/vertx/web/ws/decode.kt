package vertx.web.ws

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils

fun main(args: Array<String>) {
    val code = "dWb8iBXDEuWZUmKB0qvLng=="
    val hash = DigestUtils.sha1(code + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
    Base64.encodeBase64String(hash).also(::println)
}