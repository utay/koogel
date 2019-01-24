package eventbus

import com.google.gson.Gson
import com.mashape.unirest.http.Unirest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.kotlin.Http
import spark.kotlin.ignite

class Server {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Server::class.java)
    }

    private val http: Http = ignite().port(5000)
    private val channels: HashMap<String, ArrayList<String>> = HashMap()

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
            val cb = CallbackMessage()
            parseBody(request.body()) {
                cb.channel = it.channel
                if (channels[it.channel] == null) {
                    LOGGER.error("Channel ${it.channel} does not exist")
                    cb.nbListener = 0
                }
                else {
                    val urls = channels[it.channel]!!
                    for (url in urls) {
                        try {
                            val message = Gson().toJson(it)
                            val response = Unirest.post(url).body(message).asString()
                            if (response.status != 200) {
                                LOGGER.error("Got ${response.status}, expected 200")
                            }
                        } catch (e: Exception) {
                            LOGGER.error("Event received: ${e.message}")
                        }
                    }
                    cb.nbListener = urls.size
                }
            }
            Gson().toJson(cb)
        }
    }
}
