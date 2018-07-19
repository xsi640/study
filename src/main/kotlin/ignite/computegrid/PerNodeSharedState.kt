package ignite.computegrid

import jdk.nashorn.internal.objects.NativeFunction.call
import org.apache.ignite.Ignite
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.lang.IgniteCallable
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import jdk.nashorn.internal.objects.NativeFunction.call
import jdk.nashorn.internal.objects.NativeFunction.call
import org.apache.ignite.cluster.ClusterGroup




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
    val random = ignite.cluster().forRandom()
    val compute = ignite.compute(random)
    var res = compute.call(job)
    println(res)
    res = compute.call(job)
    println(res)
}

val job: IgniteCallable<Long> = object : IgniteCallable<Long> {

    @IgniteInstanceResource
    val ignite: Ignite? = null

    override fun call(): Long {
        val nodeLocalMap = ignite!!.cluster().nodeLocalMap<String, AtomicLong>()
        var cntr = nodeLocalMap.get("counter")
        if (cntr == null) {
            val old = nodeLocalMap.computeIfAbsent("counter") {
                cntr = AtomicLong()
                cntr
            }
            if (old != null) {
                cntr = old
            }
        }
        return cntr!!.incrementAndGet()
    }
}