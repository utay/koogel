package eventsourcing

class Event<T>(val type: String, val obj: T) {
}
