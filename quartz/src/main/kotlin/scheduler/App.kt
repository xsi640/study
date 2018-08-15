package scheduler

import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.impl.StdSchedulerFactory
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger


fun main(args: Array<String>) {
    try {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()

        val job = newJob(HelloJob::class.java)
                .withIdentity("job")
                .build()

        // Trigger the job to run now, and then repeat every 40 seconds
        val trigger = newTrigger()
                .withIdentity("trigger")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(20)
                        .repeatForever())
                .build()

        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger)

        scheduler.start()

//        scheduler.shutdown()

    } catch (se: SchedulerException) {
        se.printStackTrace()
    }

}

class HelloJob : Job {
    override fun execute(context: JobExecutionContext?) {
        Thread.sleep(20000)
        println("execute job")
    }

}