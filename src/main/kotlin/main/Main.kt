package main

import crawler.CrawlerApp
import eventbus.Client
import eventbus.Server
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import server.crawler.CrawlerManager
import sprink.Scope
import java.lang.System.exit

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Logger.getLogger("org").level = Level.ERROR
    Logger.getLogger("akka").level = Level.ERROR
    if (args.size != 3) {
        println("usage: ./bin [crawler | indexer | store | crawler_manager | bus] SERVER_HOST PORT")
        exit(1)
    }

    val module = args[0]
    val scope = createScope(args[1], args[2].toInt())

    when (module) {
        "crawler" -> runCrawler(scope)
        "indexer" -> runIndexer(scope)
        "store" -> runStore(scope)
        "crawler_manager" -> runCrawlerManager()
        "bus" -> runBus()
    }
}

fun createScope(s: String, port: Int): Scope {
    //create event bus client
    return Scope()
}

fun runCrawlerManager() {
    //TODO: Add sprink
    val crawlerManager = CrawlerManager(Client("localhost", 3000, "http://localhost:5000"))
    crawlerManager.run()
}

fun runStore(scope: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runIndexer(scope: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runCrawler(s: Scope) {
    val crawlerApp = CrawlerApp(Client("localhost", 3200, "http://localhost:5000"))
    crawlerApp.run()
}

fun runBus() {
    val bus = Server()
    bus.run()
}
