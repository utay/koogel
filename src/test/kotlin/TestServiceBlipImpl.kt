class TestServiceBlipImpl : TestService {
    override fun ping() {
        println("ping")
    }

    override fun pong() {
        println("pong")
    }
}