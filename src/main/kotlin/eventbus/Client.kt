package eventbus

import com.google.gson.Gson
import com.mashape.unirest.http.Unirest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.kotlin.Http
import spark.kotlin.RouteHandler
import spark.kotlin.ignite

class Client(val host: String, val port: Int, private val eventBusUrl: String) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Client::class.java)
    }

    private val http: Http = ignite().port(port)

    fun addHandler(path: String, routeHandler: RouteHandler.() -> Any) {
        http.post(path) {
            routeHandler()
        }
    }

    fun publish(eventMessage: EventMessage) {
        val serializedMessage = Gson().toJson(eventMessage)
        try {
            Unirest.post("$eventBusUrl/event").header("accept", "application/json")
                .body(serializedMessage).asStringAsync()
            LOGGER.info("Published to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error("Publish error: ${e.message}")
        }
    }

    fun subscribe(channel: String, callbackUrl: String) {
        val subscribeMessage = SubscribeMessage(callbackUrl)
        val eventMessage = EventMessage(channel, "SUBSCRIBE", Gson().toJson(subscribeMessage))
        try {
            Unirest.post("$eventBusUrl/subscribe").header("accept", "application/json")
                .body(Gson().toJson(eventMessage)).asStringAsync()
            LOGGER.info("Subscribed to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error("Subscribe error: ${e.message}")
        }
    }

    fun unsubscribe(channel: String, callbackUrl: String) {
        val subscribeMessage = SubscribeMessage(callbackUrl)
        val eventMessage = EventMessage(channel, "UNSUBSCRIBE", Gson().toJson(subscribeMessage))
        try {
            Unirest.post("$eventBusUrl/unsubscribe").header("accept", "application/json")
                .body(Gson().toJson(eventMessage)).asStringAsync()
            LOGGER.info("Unsubscribed to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error("Unsubscribe error: ${e.message}")
        }
    }

}