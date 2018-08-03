package ignite.cluster

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.events.DiscoveryEvent
import org.apache.ignite.events.EventType
import org.apache.ignite.lang.IgnitePredicate
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
    val cluster = ignite.cluster().forClients()
    val oldestNode = cluster.forOldest()

    ignite.events(cluster).localListen(IgnitePredicate<DiscoveryEvent> {
        println("added regardless of whether local node is in this cluster group or not.")
        true
    }, EventType.EVT_NODE_JOINED, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_FAILED)
    while (true) {
        oldestNode.run {
            println(oldestNode.node().id())
        }
        ignite.cluster().nodes().forEach {
            println("id:${it.id()}")
        }
        println("isLeader:${cluster.forOldest().node() == ignite.cluster().localNode()}")
        Thread.sleep(3000)
    }
}