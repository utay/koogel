package eventbus

data class EventMessage(val channel: String, val type: String, val obj: String)