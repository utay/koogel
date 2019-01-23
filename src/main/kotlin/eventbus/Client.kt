package eventbus

import com.google.gson.Gson
import com.mashape.unirest.http.Unirest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Client(val hostname: String, val port: Int, val eventBusUrl: String) {

    private val LOGGER: Logger = LoggerFactory.getLogger(Client::class.java)

    fun publish(eventMessage: EventMessage) {
        val serializedMessage = Gson().toJson(eventMessage)
        try {
            Unirest.post(eventBusUrl).body(serializedMessage).asJson()
            LOGGER.info("Published to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error(e.message)
        }
    }

}