package eventbus

data class CallbackMessage(var channel: String? = null, var nbListener: Int? = null)
