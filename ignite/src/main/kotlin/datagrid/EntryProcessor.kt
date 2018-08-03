package datagrid

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheEntryProcessor
import javax.cache.processor.MutableEntry


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

    val cache = ignite.getOrCreateCache<String, Int>("mycache")

    // Increment cache value 10 times.
    for (i in 0..9) {
        cache.invoke("mycache", object : CacheEntryProcessor<String, Int, Any> {
            override fun process(entry: MutableEntry<String, Int>, vararg args: Any?): Any? {
                val `val` = entry.getValue()

                entry.setValue(if (`val` == null) 1 else `val` + 1)

                return null
            }
        })
    }
}
