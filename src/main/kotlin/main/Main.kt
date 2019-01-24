package main

import application.CrawlerApp
import eventbus.Client
import eventbus.Server
import application.IndexerApp
import application.RetroIndexApp
import eventbus.EventBusClient
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import server.crawler.CrawlerManager
import server.indexer.IndexerManager
import sprink.Scope
import sprink.Sprink
import sprink.sprink
import java.lang.System.exit
import kotlin.reflect.jvm.javaMethod

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    Logger.getLogger("org").level = Level.ERROR
    Logger.getLogger("akka").level = Level.ERROR
    if (args.size != 3) {
        println("usage: ./bin [crawler | indexer | store | crawler_manager | crawler_manager | bus | retro_index] SERVER_HOST PORT")
        exit(1)
    }

    val module = args[0]
    val host = args[1]
    val port = args[2].toInt()


    val sprink = createSprink(host, port, "http://localhost:5000")

    when (module) {
        "crawler" -> runCrawler(sprink)
        "indexer" -> runIndexer(sprink)
        "store" -> runStore(sprink)
        "crawler_manager" -> runCrawlerManager(sprink)
        "indexer_manager" -> runIndexerManager(sprink)
        "bus" -> runBus()
        "retro_index" -> runRetroIndex(sprink)
    }
}

fun createSprink(host: String, port: Int, eventBusUrl: String): Sprink {
    val eventBusClient = Client(host, port, eventBusUrl)
    return sprink {
        scope {
            bean(EventBusClient::class.java, eventBusClient)
        }
    }
}

fun runIndexerManager(sprink: Sprink) {
    val indexerManager = IndexerManager(sprink.instanceOf(EventBusClient::class.java))
    indexerManager.run()
}

fun runCrawlerManager(sprink: Sprink) {
    val crawlerManager = CrawlerManager(sprink.instanceOf(EventBusClient::class.java))
    crawlerManager.run()
}

fun runRetroIndex(sprink: Sprink) {
    val retroIndexApp = RetroIndexApp(sprink.instanceOf(EventBusClient::class.java))
    retroIndexApp.run()
}

fun runStore(sprink: Sprink) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun runIndexer(sprink: Sprink) {
    val indexerApp = IndexerApp(sprink.instanceOf(EventBusClient::class.java))
    indexerApp.run()
}

fun runCrawler(sprink: Sprink) {
    val crawlerApp = CrawlerApp(sprink.instanceOf(EventBusClient::class.java))
    crawlerApp.run()
}

fun runBus() {
    val bus = Server()
    bus.run()
}
