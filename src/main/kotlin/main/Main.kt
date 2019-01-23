package main

import crawler.CrawlerApp
import eventbus.Client
import eventbus.Server
import indexer.IndexerApp
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import server.crawler.CrawlerManager
import server.indexer.IndexerManager
import sprink.Scope
import java.lang.System.exit

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Logger.getLogger("org").level = Level.ERROR
    Logger.getLogger("akka").level = Level.ERROR
    if (args.size != 3) {
        println("usage: ./bin [crawler | indexer | store | crawler_manager | crawler_manager | bus] SERVER_HOST PORT")
        exit(1)
    }

    val module = args[0]
    val host = args[1]
    val port = args[2].toInt()
    val scope = createScope(host, port)

    when (module) {
        "crawler" -> runCrawler(scope, port)
        "indexer" -> runIndexer(scope, port)
        "store" -> runStore(scope)
        "crawler_manager" -> runCrawlerManager(port)
        "indexer_manager" -> runIndexerManager(port)
        "bus" -> runBus()
    }
}

fun createScope(s: String, port: Int): Scope {
    //create event bus client
    return Scope()
}

fun runIndexerManager(port: Int) {
    //TODO: Add sprink
    val indexerManager = IndexerManager(Client("localhost", port, "http://localhost:5000"))
    indexerManager.run()
}

fun runCrawlerManager(port: Int) {
    //TODO: Add sprink
    val crawlerManager = CrawlerManager(Client("localhost", port, "http://localhost:5000"))
    crawlerManager.run()
}

fun runStore(scope: Scope) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runIndexer(scope: Scope, port: Int) {
    val indexerApp = IndexerApp(Client("localhost", port, "http://localhost:5000"))
    indexerApp.run()
}

fun runCrawler(s: Scope, port: Int) {
    val crawlerApp = CrawlerApp(Client("localhost", port, "http://localhost:5000"))
    crawlerApp.run()
}

fun runBus() {
    val bus = Server()
    bus.run()
}
