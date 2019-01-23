package crawler

import Utils
import application.App
import com.google.gson.Gson
import eventbus.Client
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.crawler.CrawlEndedSerializer
import server.crawler.CrawlSerializer

class CrawlerApp(eventBus: Client) : App(eventBus) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CrawlerApp::class.java)
    }

    private val crawler: Crawler = Crawler()

    init {
        eventBus.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    "CRAWL" -> startCrawling(Gson().fromJson(it.obj, CrawlSerializer::class.java))
                }
            }
        }
        eventBus.subscribe("crawl_$uid", "http://${eventBus.host}:${eventBus.port}/event")
    }

    override fun run() {
        LOGGER.info("Crawler '$uid' started")
    }

    private fun startCrawling(crawlSerializer: CrawlSerializer) {
        val res = crawler.crawl(crawlSerializer.url)
        if (res == null) {
            LOGGER.error("Crawling failed for url '${crawlSerializer.url}'")
            return
        }
        //TODO: call indexerManager
        val message = Gson().toJson(CrawlEndedSerializer(res.second, crawlSerializer.url))
        eventBus.publish(EventMessage("crawler_manager", "CRAWL_ENDED", message))
    }
}