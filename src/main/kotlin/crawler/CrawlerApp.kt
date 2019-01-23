package crawler

import Utils
import application.App
import com.google.gson.Gson
import eventbus.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.crawler.CrawlEndedSerializer
import server.crawler.CrawlSerializer
import server.crawler.CrawlerCommand.Companion.CRAWL
import server.crawler.CrawlerCommand.Companion.CRAWL_ENDED
import server.crawler.CrawlerCommand.Companion.REGISTER_CRAWLER
import server.crawler.CrawlerManager.Companion.CRAWLER_MANAGER_CHANNEL
import server.crawler.RegisterCrawlerSerializer

class CrawlerApp(eventBus: Client) : App(eventBus) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CrawlerApp::class.java)
    }

    private val crawler: Crawler = Crawler()

    init {
        eventBus.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    CRAWL -> startCrawling(Gson().fromJson(it.obj, CrawlSerializer::class.java))
                }
            }
        }
        eventBus.subscribe("crawl_$uid", "http://${eventBus.host}:${eventBus.port}/event")
        sendMessage(CRAWLER_MANAGER_CHANNEL, REGISTER_CRAWLER, RegisterCrawlerSerializer(uid))
    }

    override fun run() {
        LOGGER.info("Crawler '$uid' started")
    }

    private fun startCrawling(crawlSerializer: CrawlSerializer) {
        val res = crawler.crawl(crawlSerializer.url)
        if (res == null) {
            LOGGER.error("Crawling failed for url '${crawlSerializer.url}'")
            sendMessage(CRAWLER_MANAGER_CHANNEL, CRAWL_ENDED, CrawlEndedSerializer(ArrayList(), crawlSerializer.url, uid))
            return
        }
        //TODO: call indexerManager
        sendMessage(CRAWLER_MANAGER_CHANNEL, CRAWL_ENDED, CrawlEndedSerializer(res.second, crawlSerializer.url, uid))
    }
}