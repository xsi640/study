package datagrid

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.events.CacheEvent
import org.apache.ignite.events.Event
import org.apache.ignite.events.EventType
import org.apache.ignite.lang.IgnitePredicate
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

val CACHE_NAME = "CACHE_TEST"

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
    val finder = TcpDiscoveryVmIpFinder()
    finder.setAddresses(listOf("127.0.0.1:47500..47505"))
    tcpDiscoverySpi.ipFinder = finder
    cfg.discoverySpi = tcpDiscoverySpi

    val ignite = Ignition.start(cfg)


    // Local listener that listenes to local events.
    val locLsnr: (Event) -> Boolean = { evt ->
        val cacheEvent = evt as CacheEvent
        println("Received event [evt=" + cacheEvent.name() + ", key=" + cacheEvent.key() +
                ", oldVal=" + cacheEvent.oldValue() + ", newVal=" + cacheEvent.newValue())

        true // Continue listening.
    }

    // Subscribe to specified cache events occuring on local node.
    ignite.events(ignite.cluster()).localListen(IgnitePredicate(locLsnr),
            EventType.EVT_CACHE_OBJECT_PUT,
            EventType.EVT_CACHE_OBJECT_READ,
            EventType.EVT_CACHE_OBJECT_REMOVED)

    // Get an instance of named cache.
    val cache = ignite.getOrCreateCache<Int, String>("cacheName")

    // Generate cache events.
    for (i in 0..19)
        cache.put(i, Integer.toString(i))
}