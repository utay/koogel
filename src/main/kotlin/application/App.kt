package application

import com.google.gson.Gson
import eventbus.CallbackMessage
import eventbus.Client
import eventbus.EventBusClient
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import store.EventStore.Companion.STORE_CHANNEL
import store.EventStoreCommand.Companion.ADD_EVENT
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


abstract class App protected constructor(protected val eventBus: EventBusClient) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(App::class.java)
    }

    protected val uid: String = UUID.randomUUID().toString()

    private fun sendToStore(eventMessage: EventMessage) {
        try {
            val storeMessage = EventMessage(STORE_CHANNEL, ADD_EVENT, Gson().toJson(eventMessage))
            eventBus.publish(storeMessage)
        } catch (e: Exception) {
            LOGGER.error("Impossible to send message to store: ${e.message}")
        }
    }

    protected fun sendMessage(channel: String, type: String, obj: Any): CallbackMessage? {
        try {
            val em = EventMessage(channel, type, Gson().toJson(obj))
            return eventBus.publish(em)
        } catch (e: Exception) {
            LOGGER.error("Impossible to send message: {}", e.message)
            return null
        }
    }

    abstract fun run()
}
