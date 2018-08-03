package datagrid

import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.CacheConfiguration
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

    cfg.cacheConfiguration.get(0).sqlFunctionClasses

    val ignite = Ignition.start(cfg)

    var cacheConfiguration = CacheConfiguration<String, String>()
    cacheConfiguration.name = "myDynamicCache"
    cacheConfiguration.atomicityMode = CacheAtomicityMode.TRANSACTIONAL
    var cache = ignite.getOrCreateCache(cacheConfiguration)
    cache.put("1", "zhangsan")
    cache.get("1").also(::println)
}