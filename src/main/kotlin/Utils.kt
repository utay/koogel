import com.google.gson.Gson
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Utils {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Utils::class.java)

        fun parseBody(body: String, handler: (message: EventMessage) -> Unit) {
            val eventMessage = Gson().fromJson(body, EventMessage::class.java)
            if (eventMessage == null) {
                LOGGER.error("Body cannot be null")
            } else {
                handler(eventMessage)
            }
        }
    }
}