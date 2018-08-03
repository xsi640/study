package cronutils

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


fun main(args: Array<String>) {
    val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
    val cronParser = CronParser(cronDefinition)
    val cron = cronParser.parse("* 0/4 * * * ? *")
    cron.validate()
    val now = ZonedDateTime.now()
    println("now:${toStrig(now)}")
    val executionTime = ExecutionTime.forCron(cron)
    val lastExecution = executionTime.lastExecution(now)
    println("lastTime:${toStrig(lastExecution.get())}")
    val nextExecution = executionTime.nextExecution(now)
    println("lastTime:${toStrig(nextExecution.get())}")
    val timeFromLastExecution = executionTime.timeFromLastExecution(now)
    println("lastTime:${timeFromLastExecution}")
    val timeToNextExecution = executionTime.timeToNextExecution(now)
    println("lastTime:${timeToNextExecution}")
}

fun toStrig(time: ZonedDateTime) {
    print(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(time))
}