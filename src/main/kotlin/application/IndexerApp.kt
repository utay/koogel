package application

import com.google.gson.Gson
import eventbus.Client
import indexer.Indexer
import application.RetroIndexApp.Companion.RETRO_INDEX_CHANNEL
import eventbus.EventBusClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.indexer.AddDocumentToIndexSerializer
import server.indexer.EndIndexSerializer
import server.indexer.IndexPageSerializer
import server.indexer.IndexerCommand.Companion.ADD_DOCUMENT_TO_INDEX
import server.indexer.IndexerCommand.Companion.END_INDEXING
import server.indexer.IndexerCommand.Companion.INDEX_PAGE
import server.indexer.IndexerCommand.Companion.REGISTER_INDEXER
import server.indexer.IndexerManager.Companion.INDEXER_MANAGER_CHANNEL
import server.indexer.RegisterIndexerSerializer

class IndexerApp(eventBusClient: EventBusClient): App(eventBusClient) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(IndexerApp::class.java)
    }

    private val indexer: Indexer = Indexer()

    init {
        eventBusClient.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    INDEX_PAGE -> indexPage(Gson().fromJson(it.obj, IndexPageSerializer::class.java))
                }
            }
        }
        eventBusClient.subscribe("indexer_$uid", eventBusClient.getCallBackURL("/event"))
        sendMessage(INDEXER_MANAGER_CHANNEL, REGISTER_INDEXER, RegisterIndexerSerializer(uid))
    }

    override fun run() {
        LOGGER.info("Indexer '$uid' created")
    }

    private fun indexPage(indexPageSerializer: IndexPageSerializer) {
        LOGGER.info("Index page with url '${indexPageSerializer.page.URL}'")
        val document = indexer.getDocument(indexPageSerializer.page)
        sendMessage(RETRO_INDEX_CHANNEL, ADD_DOCUMENT_TO_INDEX, AddDocumentToIndexSerializer(document))
        sendMessage(INDEXER_MANAGER_CHANNEL, END_INDEXING, EndIndexSerializer(uid))
    }
}