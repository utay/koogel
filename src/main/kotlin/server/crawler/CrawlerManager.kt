package server.crawler

import Utils
import application.App
import com.google.gson.Gson
import eventbus.Client
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashSet

class CrawlerManager(eventBus: Client) : App(eventBus) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CrawlerManager::class.java)
    }

    private val crawlers = hashMapOf<String, Boolean>()
    private val urlQueue = LinkedBlockingQueue<String>()
    private val urlSeen = HashSet<String>()

    init {
        urlQueue.add("http://www.wikipedia.com")
        eventBus.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    "REGISTER_CRAWLER" -> registerCrawler(
                        Gson().fromJson(
                            it.obj,
                            RegisterCrawlerSerializer::class.java
                        )
                    )
                    "CRAWL_ENDED" -> addLinksToCrawl(Gson().fromJson(it.obj, CrawlEndedSerializer::class.java))
                }
            }
        }
        eventBus.subscribe("crawler_manager", "http://${eventBus.host}:${eventBus.port}/event")
    }

    private fun registerCrawler(registerCrawlerSerializer: RegisterCrawlerSerializer) {
        crawlers[registerCrawlerSerializer.crawlerId] = true
        LOGGER.info("Register crawler '${registerCrawlerSerializer.crawlerId}'")
    }

    private fun addLinksToCrawl(crawlEndedSerializer: CrawlEndedSerializer) {
        urlSeen.add(crawlEndedSerializer.url)
        val list = crawlEndedSerializer.urls.filter { !urlSeen.contains(it) }.distinct()
        LOGGER.info("Received urls from '${crawlEndedSerializer.id}', numbers: ${list.size}")
        urlQueue.addAll(list)
        crawlers[crawlEndedSerializer.id] = true
    }

    override fun run() {
        while (true) {
            val crawler = crawlers.filter { it.value }.keys.firstOrNull() ?: continue
            val url = urlQueue.poll() ?: continue
            crawlers[crawler] = false
            val message = Gson().toJson(CrawlSerializer(url))
            eventBus.publish(EventMessage("crawl_$crawler", "CRAWL", message))
        }
    }
}