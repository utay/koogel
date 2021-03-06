package server.indexer

import Utils
import application.App
import com.google.gson.Gson
import crawler.Page
import eventbus.EventBusClient
import eventbus.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.indexer.IndexerCommand.Companion.ADD_PAGE_TO_INDEX
import server.indexer.IndexerCommand.Companion.END_INDEXING
import server.indexer.IndexerCommand.Companion.INDEX_PAGE
import server.indexer.IndexerCommand.Companion.REGISTER_INDEXER
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque

class IndexerManager(eventBusClient: EventBusClient) : App(eventBusClient) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(IndexerManager::class.java)
        const val INDEXER_MANAGER_CHANNEL = "indexer_manager"
    }

    private val pagesToIndex = LinkedBlockingDeque<Page>()
    private val indexers = ConcurrentHashMap<String, Boolean>()

    init {
        eventBusClient.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    ADD_PAGE_TO_INDEX -> addPageToIndex(Gson().fromJson(it.obj, AddPageToIndexSerializer::class.java))
                    REGISTER_INDEXER -> registerIndexer(Gson().fromJson(it.obj, RegisterIndexerSerializer::class.java))
                    END_INDEXING -> endIndexing(Gson().fromJson(it.obj, EndIndexSerializer::class.java))
                }
            }
        }
        eventBusClient.subscribe(INDEXER_MANAGER_CHANNEL, eventBusClient.getCallBackURL("/event"))
    }

    override fun run() {
        while (true) {
            val indexer = indexers.filter { it.value }.keys.firstOrNull() ?: continue
            val value = pagesToIndex.poll() ?: continue
            indexers[indexer] = false
            val message = IndexPageSerializer(value)
            eventBus.publish(EventMessage("indexer_$indexer", INDEX_PAGE, Gson().toJson(message)))
        }
    }

    private fun addPageToIndex(addPageToIndexSerializer: AddPageToIndexSerializer) {
        pagesToIndex.add(addPageToIndexSerializer.page)
        LOGGER.info("Receive page with url '${addPageToIndexSerializer.page.URL}' to index")
    }

    private fun registerIndexer(indexer: RegisterIndexerSerializer) {
        indexers[indexer.id] = true
        LOGGER.info("Register indexer '${indexer.id}'")
    }

    private fun endIndexing(indexer: EndIndexSerializer) {
        indexers[indexer.id] = true
        LOGGER.info("Indexing finished for '${indexer.id}'")
    }
}