package eventbus

import spark.kotlin.RouteHandler

interface EventBusClient {
    fun publish(eventMessage: EventMessage)
    fun subscribe(channel: String, callbackUrl: String)
    fun unsubscribe(channel: String, callbackUrl: String)
    fun addHandler(path: String, routeHandler: RouteHandler.() -> Any)
    fun getCallBackURL(path: String): String
}