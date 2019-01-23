package eventsourcing

class EventStore {
    private val events: ArrayList<Event<Any>> = ArrayList()
    private val reducers: ArrayList<Reducer> = ArrayList()

    fun addReducer(reducer: Reducer) {
        reducers.add(reducer)
    }

    fun dispatch(event: Event<Any>) {
        events.add(event)
        reduceAll(event)
    }

    fun reduceAll(event: Event<Any>) {
        for (reducer in reducers) {
            reducer.reduce(event)
        }
    }
}
