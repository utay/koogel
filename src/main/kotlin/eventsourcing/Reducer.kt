package eventsourcing

interface Reducer {
    fun reduce(event: Event<Any>)
}
