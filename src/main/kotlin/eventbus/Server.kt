package eventbus

import spark.kotlin.Http
import spark.kotlin.ignite

class Server {

    private val http: Http = ignite()
    private val channels: HashMap<String, ArrayList<String>> = HashMap()

    fun run() {
        http.post("/subscribe") {
            val url = "http://localhost"
            val channel = "channel"

            if (!channels.containsKey(channel)) {
                channels[channel] = ArrayList()
            }

            channels[channel]!!.add(url)
        }

        http.post("/unsubscribe") {
            val channel = "channel"

            if (channels.containsKey(channel))
                channels.remove(channel)
        }

        http.post("/event") {
            val channel = "channel"

            // channel
            // type
            // message
            for (url in channels[channel]!!) {
            }
        }
    }
}
