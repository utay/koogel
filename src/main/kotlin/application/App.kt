package application

import com.google.gson.Gson
import eventbus.Client
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


abstract class App protected constructor(protected val eventBus: Client) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(App::class.java)
    }

    protected val uid: String = UUID.randomUUID().toString()

    protected fun sendMessage(channel: String, type: String, obj: Any) {
        try {
            val em = EventMessage(channel, type, Gson().toJson(obj))
            eventBus.publish(em)
        } catch (e: Exception) {
            LOGGER.error("Impossible to send message: {}", e.message)
        }
    }

    abstract fun run()

    protected fun retryIn(seconds: Int, consumer: Runnable) {
        LOGGER.info("Retry fetching url in {} seconds", seconds)
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.schedule(consumer, seconds.toLong(), TimeUnit.SECONDS)
        executor.shutdownNow()
    }
}