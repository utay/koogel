package eventbus

import com.google.gson.Gson
import com.mashape.unirest.http.Unirest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Spark
import spark.kotlin.Http
import spark.kotlin.RouteHandler
import spark.kotlin.ignite

class Client(val host: String, val port: Int, private val eventBusUrl: String) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Client::class.java)
    }

    private val http: Http = ignite()

    init {
        Spark.port(port)
    }

    fun addHandler(path: String, routeHandler: RouteHandler.() -> Any) {
        http.post(path) {
            routeHandler()
        }
    }

    fun publish(eventMessage: EventMessage) {
        val serializedMessage = Gson().toJson(eventMessage)
        try {
            Unirest.post("$eventBusUrl/event").body(serializedMessage).asJson()
            LOGGER.info("Published to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error(e.message)
        }
    }

    fun subscribe(channel: String, callbackUrl: String) {
        val subscribeMessage = SubscribeMessage(callbackUrl)
        val eventMessage = EventMessage(channel, subscribeMessage.javaClass.typeName, Gson().toJson(subscribeMessage))
        try {
            Unirest.post("$eventBusUrl/subscribe").body(eventMessage).asJson()
            LOGGER.info("Subscribed to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error(e.message)
        }
    }

    fun unsubscribe(channel: String, callbackUrl: String) {
        val subscribeMessage = SubscribeMessage(callbackUrl)
        val eventMessage = EventMessage(channel, subscribeMessage.javaClass.typeName, Gson().toJson(subscribeMessage))
        try {
            Unirest.post("$eventBusUrl/unsubscribe").body(eventMessage).asJson()
            LOGGER.info("Unsubscribed to channel ${eventMessage.channel}")
        } catch (e: Exception) {
            LOGGER.error(e.message)
        }
    }

}