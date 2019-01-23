package main

import org.apache.log4j.BasicConfigurator
import sprink.Scope
import java.lang.System.exit

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    if (args.size != 3) {
        println("usage: ./bin [crawler | indexer | store | server] SERVER_HOST PORT")
        exit(1)
    }

    val module = args[0]
    val scope = createScope(args[1], args[2].toInt())

    when (module) {
        "crawler" -> runCrawler(scope)
        "indexer" -> runIndexer(scope)
        "store" -> runStore(scope)
        "server" -> runServer()
    }
}

fun createScope(s: String, port: Int): Scope {
    //create event bus client
    return Scope()
}

fun runServer() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runStore(scope: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runIndexer(scope: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runCrawler(s: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
