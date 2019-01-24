package store

import application.App
import com.google.gson.Gson
import eventbus.EventBusClient
import eventbus.EventMessage
import org.slf4j.LoggerFactory
import store.EventStoreCommand.Companion.ADD_EVENT
import java.time.LocalDateTime

class EventStore(eventBusClient: EventBusClient): App(eventBusClient) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventStore::class.java)
        const val STORE_CHANNEL = "store"
    }

    private val events: ArrayList<EventMessage> = ArrayList()

    init {
        eventBusClient.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type)  {
                    ADD_EVENT -> addEvent(Gson().fromJson(it.obj, EventMessage::class.java))
                }
            }
        }
        eventBusClient.subscribe(STORE_CHANNEL, eventBusClient.getCallBackURL("/event"))
    }

    private fun addEvent(event: EventMessage) {
        LOGGER.info("Receive event: channel '${event.channel}', type '${event.type}'")
        events.add(event)
    }

    fun getEvents(): ArrayList<EventMessage> {
        return events
    }

    override fun run() {
        LOGGER.info("Event Store start at '${LocalDateTime.now()}'")
    }
}
