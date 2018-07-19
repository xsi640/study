package ignite.computegrid

import org.apache.ignite.Ignition
import org.apache.ignite.compute.*
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.resources.JobContextResource
import org.apache.ignite.resources.TaskSessionResource
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import java.util.*

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
    val cnt = compute.execute(TaskSessionAttributesTask(), "Hello Grid Enabled World!")
    println(">>> Total number of characters in the phrase is '$cnt'.")
}

@ComputeTaskSessionFullSupport
class TaskSessionAttributesTask : ComputeTaskSplitAdapter<String, Int>() {
    override fun split(gridSize: Int, arg: String): MutableCollection<out ComputeJob> {
        val words = arg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val jobs = ArrayList<ComputeJob>(words.size)

        for (word in arg.split(" ")) {
            jobs.add(object : ComputeJobAdapter(arg) {

                @TaskSessionResource
                private val ses: ComputeTaskSession? = null
                @JobContextResource
                private val jobCtx: ComputeJobContext? = null

                override fun execute(): Any? {
                    ses!!.setAttribute(jobCtx!!.jobId, "STEP1")

                    for (sibling in ses.jobSiblings) {
                        ses.waitForAttribute(sibling.jobId, "STEP1", 0)
                    }
                    return word.length
                }
            })
        }
        return jobs
    }

    override fun reduce(results: MutableList<ComputeJobResult>): Int? {
        var sum = 0

        for (res in results) {
            println(res.jobContext.attributes[res.jobContext.jobId])
            sum += res.getData<Int>()
        }

        return sum
    }

}