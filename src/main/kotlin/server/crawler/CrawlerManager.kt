package server.crawler

import Utils
import application.App
import com.google.gson.Gson
import eventbus.EventBusClient
import org.eclipse.jetty.util.ConcurrentHashSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.crawler.CrawlerCommand.Companion.CRAWL
import server.crawler.CrawlerCommand.Companion.CRAWL_ENDED
import server.crawler.CrawlerCommand.Companion.REGISTER_CRAWLER
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

class CrawlerManager(eventBus: EventBusClient) : App(eventBus) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CrawlerManager::class.java)
        const val CRAWLER_MANAGER_CHANNEL = "crawler_manager"
    }

    private val crawlers = ConcurrentHashMap<String, Boolean>()
    private val urlQueue = LinkedBlockingQueue<String>()
    private val urlSeen = ConcurrentHashSet<String>()

    init {
        urlQueue.addAll(
            arrayListOf(
                "https://moz.com/top500",
                "https://insideapp.io"
            )
        )
        eventBus.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    REGISTER_CRAWLER -> registerCrawler(
                        Gson().fromJson(
                            it.obj,
                            RegisterCrawlerSerializer::class.java
                        )
                    )
                    CRAWL_ENDED -> addLinksToCrawl(Gson().fromJson(it.obj, CrawlEndedSerializer::class.java))
                }
            }
        }
        eventBus.subscribe(CRAWLER_MANAGER_CHANNEL, eventBus.getCallBackURL("/event"))
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
            sendMessage("crawl_$crawler", CRAWL, CrawlSerializer(url))
        }
    }
}