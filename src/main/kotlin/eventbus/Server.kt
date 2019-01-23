package eventbus

import com.google.gson.Gson
import com.mashape.unirest.http.Unirest
import indexer.Indexer
import org.apache.log4j.BasicConfigurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.kotlin.Http
import spark.kotlin.ignite

class Server {

    private val http: Http = ignite()
    private val channels: HashMap<String, ArrayList<String>> = HashMap()
    private val LOGGER: Logger = LoggerFactory.getLogger(Indexer::class.java)

    private fun parseBody(body: String, handler: (message: EventMessage) -> Unit) {
        val eventMessage = Gson().fromJson(body, EventMessage::class.java)
        if (eventMessage == null) {
            LOGGER.error("Subscription body cannot be null")
        } else {
            handler(eventMessage)
        }
    }

    fun run() {
        http.post("/subscribe") {
            parseBody(request.body()) {
                if (!channels.containsKey(it.channel)) {
                    channels[it.channel] = ArrayList()
                }
                val subscribeMessage = Gson().fromJson<SubscribeMessage>(it.obj, SubscribeMessage::class.java)
                channels[it.channel]!!.add(subscribeMessage.callbackUrl)
                LOGGER.info("${subscribeMessage.callbackUrl} has subscribed to ${it.channel}")
            }
        }

        http.post("/unsubscribe") {
            parseBody(request.body()) {
                val unsubscribeMessage = Gson().fromJson<SubscribeMessage>(it.obj, SubscribeMessage::class.java)
                if (channels.containsKey(it.channel)) {
                    channels[it.channel]?.remove(unsubscribeMessage.callbackUrl)
                    LOGGER.info("${unsubscribeMessage.callbackUrl} has unsubscribed to ${it.channel}")
                } else {
                    LOGGER.error("Channel ${it.channel} does not exist")
                }
            }
        }

        http.post("/event") {
            parseBody(request.body()) {
                for (url in channels[it.channel]!!) {
                    try {
                        val response = Unirest.post(url).body(it).asJson()
                        if (response.status != 200) {
                            LOGGER.error("Got ${response.status}, expected 200")
                        }
                    } catch (e: Exception) {
                        LOGGER.error(e.message)
                    }
                }
            }
        }
    }
}
