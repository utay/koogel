package indexer

import application.App
import com.google.gson.Gson
import eventbus.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import server.indexer.AddDocumentToIndexSerializer
import server.indexer.IndexerCommand.Companion.ADD_DOCUMENT_TO_INDEX

class RetroIndexApp(val eventBusClient: Client): App(eventBusClient) {

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
        val callBackUrl = "http://${eventBusClient.host}:${eventBusClient.port}/event"
        eventBusClient.subscribe(RETRO_INDEX_CHANNEL, callBackUrl)
    }

    override fun run() {
        LOGGER.info("RetroIndex '$uid' created")
    }

    private fun addDocument(addDocumentToIndexSerializer: AddDocumentToIndexSerializer) {
        index.addDocument(addDocumentToIndexSerializer.document)
        LOGGER.info("Add document with url ${addDocumentToIndexSerializer.document.URL}")
    }

}