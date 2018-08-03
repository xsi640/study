package computegrid

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.compute.ComputeJobResult
import org.apache.ignite.compute.ComputeJobAdapter
import java.util.ArrayList
import org.apache.ignite.compute.ComputeJob
import org.apache.ignite.compute.ComputeTaskSplitAdapter

/**
 * mapreduce
 */
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

    val cnt = compute.execute(CharacterCountTask::class.java, "Hello Grid Enabled World!")

    println(">>> Total number of characters in the phrase is '$cnt'.")
}

private class CharacterCountTask : ComputeTaskSplitAdapter<String, Int>() {
    // 1. Splits the received string into to words
    // 2. Creates a child job for each word
    // 3. Sends created jobs to other nodes for processing.
    public override fun split(gridSize: Int, arg: String): MutableCollection<out ComputeJob>? {
        val words = arg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val jobs = ArrayList<ComputeJob>(words.size)

        for (word in arg.split(" ")) {
            jobs.add(object : ComputeJobAdapter() {
                override fun execute(): Any {
                    println(">>> Printing '$word' on from compute job.")

                    // Return number of letters in the word.
                    return word.length
                }
            })
        }

        return jobs
    }

    override fun reduce(results: List<ComputeJobResult>): Int? {
        var sum = 0

        for (res in results)
            sum += res.getData<Int>()

        return sum
    }
}