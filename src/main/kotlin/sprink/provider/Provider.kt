package sprink.provider

interface Provider<T> {
    fun getInstance(klass: Class<T>): T
    fun clean() = {}
}