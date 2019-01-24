package application

import Utils
import com.google.gson.Gson
import crawler.Crawler
import eventbus.Client
import eventbus.EventBusClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.crawler.CrawlEndedSerializer
import server.crawler.CrawlSerializer
import server.crawler.CrawlerCommand.Companion.CRAWL
import server.crawler.CrawlerCommand.Companion.CRAWL_ENDED
import server.crawler.CrawlerCommand.Companion.REGISTER_CRAWLER
import server.crawler.CrawlerManager.Companion.CRAWLER_MANAGER_CHANNEL
import server.crawler.RegisterCrawlerSerializer
import server.indexer.AddPageToIndexSerializer
import server.indexer.IndexerCommand.Companion.ADD_PAGE_TO_INDEX
import server.indexer.IndexerManager.Companion.INDEXER_MANAGER_CHANNEL

class CrawlerApp(eventBus: EventBusClient) : App(eventBus) {

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
        eventBus.subscribe("crawl_$uid", eventBus.getCallBackURL("/event"))
        sendMessage(CRAWLER_MANAGER_CHANNEL, REGISTER_CRAWLER, RegisterCrawlerSerializer(uid))
    }

    override fun run() {
        LOGGER.info("Crawler '$uid' started")
    }

    private fun startCrawling(crawlSerializer: CrawlSerializer) {
        val res = crawler.crawl(crawlSerializer.url)
        if (res == null) {
            LOGGER.error("Crawling failed for url '${crawlSerializer.url}'")
            sendMessage(
                CRAWLER_MANAGER_CHANNEL,
                CRAWL_ENDED,
                CrawlEndedSerializer(ArrayList(), crawlSerializer.url, uid)
            )
            return
        }
        sendMessage(INDEXER_MANAGER_CHANNEL, ADD_PAGE_TO_INDEX, AddPageToIndexSerializer(res.first))
        sendMessage(CRAWLER_MANAGER_CHANNEL, CRAWL_ENDED, CrawlEndedSerializer(res.second, crawlSerializer.url, uid))
    }
}