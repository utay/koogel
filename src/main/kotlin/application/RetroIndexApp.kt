package application

import com.google.gson.Gson
import eventbus.Client
import eventbus.EventBusClient
import indexer.Index
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.indexer.AddDocumentToIndexSerializer
import server.indexer.IndexerCommand.Companion.ADD_DOCUMENT_TO_INDEX

class RetroIndexApp(val eventBusClient: EventBusClient): App(eventBusClient) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RetroIndexApp::class.java)
        const val RETRO_INDEX_CHANNEL = "retro_index"
    }

    private val index = Index()

    init {
        eventBusClient.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    ADD_DOCUMENT_TO_INDEX -> addDocument(Gson().fromJson(it.obj, AddDocumentToIndexSerializer::class.java))
                }
            }
        }
        //TODO: Add handler for call from search
        eventBusClient.subscribe(RETRO_INDEX_CHANNEL, eventBusClient.getCallBackURL("/event"))
    }

    override fun run() {
        LOGGER.info("RetroIndex '$uid' created")
    }

    private fun addDocument(addDocumentToIndexSerializer: AddDocumentToIndexSerializer) {
        index.addDocument(addDocumentToIndexSerializer.document)
        LOGGER.info("Add document with url ${addDocumentToIndexSerializer.document.URL}")
    }

}