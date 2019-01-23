package application

import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID


abstract class App protected constructor(/*protected val eventBus: EventBusClient*/) {

    protected val uid: String = UUID.randomUUID().toString()

    abstract fun run()

    protected fun sendMessage(channel: String, obj: Any) {
       /* try {
            val em = EventMessage(channel, obj)
            eventBus.publish(em)
        } catch (e: JsonProcessingException) {
            LOGGER.error("Impossible to send message: {}", e.getMessage())
        }*/
    }

    protected fun retryIn(seconds: Int, consumer: Runnable) {
        LOGGER.info("Retry fetching url in {}seconds", seconds)
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule(consumer, seconds.toLong(), TimeUnit.SECONDS)
        executor.shutdownNow()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(App::class.java)
    }
}