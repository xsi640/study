package ignite.datagrid

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

fun main(args: Array<String>) {
    val cfg = IgniteConfiguration()
    cfg.isClientMode = true
    val tcpCommunicationSpi = TcpCommunicationSpi()
    tcpCommunicationSpi.messageQueueLimit = 4096
    tcpCommunicationSpi.localAddress = "127.0.0.1"
    tcpCommunicationSpi.localPort = 47200
    cfg.communicationSpi = tcpCommunicationSpi
    val tcpDiscoverySpi = TcpDiscoverySpi()
    tcpDiscoverySpi.localAddress = "127.0.0.1"
    tcpDiscoverySpi.localPort = 47600
    var finder = TcpDiscoveryVmIpFinder()
    finder.setAddresses(listOf("127.0.0.1:47500..47505"))
    tcpDiscoverySpi.ipFinder = finder
    cfg.discoverySpi = tcpDiscoverySpi

    val ignite = Ignition.start(cfg)

    val cache = ignite.getOrCreateCache<String, String>("myCache")
    cache.put("1", "zhangsan")
    println(cache.get("1"))

    //get如果存在返回值，如果不存在就put新值
    val v = cache.getAndPutIfAbsent("2", "lisi")
    println(v)

    //如果不存在put新值，返回值，是否put了新值
    val success = cache.putIfAbsent("3", "wangwu")
    println(success)
    println(cache.get("3"))

    //如果存在，更新值，返回旧值
    val v2 = cache.getAndReplace("3", "zhaoliu")
    println(v2)
    println(cache.get("3"))

    //更新一个值，返回是否更新了值
    val success2 = cache.replace("3", "ssss")
    println(success2)
    println(cache.get("3"))

    //更新一个匹配的值
    val success3 = cache.replace("3", "ssss", "dddd")
    println(success3)
    println(cache.get("3"))

    //删除一个值
    val success4= cache.remove("3")
    println(success4)
    println(cache.get("3"))
}