package ignite.computegrid

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.compute.ComputeJobResult
import org.apache.ignite.compute.ComputeJobAdapter
import org.apache.ignite.cluster.ClusterNode
import java.util.HashMap
import org.apache.ignite.compute.ComputeJob
import org.apache.ignite.compute.ComputeTaskAdapter


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
    val clientGroup = ignite.cluster().forClients()
    val compute = ignite.compute(clientGroup)

    val cnt = compute.execute(CharacterCountTask2::class.java, "Hello Grid Enabled World!")

    println(">>> Total number of characters in the phrase is '$cnt'.")
}

class CharacterCountTask2 : ComputeTaskAdapter<String, Int>() {
    // 1. Splits the received string into to words
    // 2. Creates a child job for each word
    // 3. Sends created jobs to other nodes for processing.
    override fun map(subgrid: List<ClusterNode>, arg: String?): Map<out ComputeJob, ClusterNode>? {
        val words = arg!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val map = HashMap<ComputeJob, ClusterNode>(words.size)

        var it = subgrid.iterator()

        for (word in arg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            // If we used all nodes, restart the iterator.
            if (!it.hasNext())
                it = subgrid.iterator()

            val node = it.next()

            map[object : ComputeJobAdapter() {
                override fun execute(): Any {
                    println(">>> Printing '$word' on this node from grid job.")

                    // Return number of letters in the word.
                    return word.length
                }
            }] = node
        }

        return map
    }

    override fun reduce(results: List<ComputeJobResult>): Int? {
        var sum = 0

        for (res in results)
            sum += res.getData<Int>()

        return sum
    }
}