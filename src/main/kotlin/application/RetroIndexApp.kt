package application

import Utils
import com.google.gson.Gson
import crawler.Lexer
import eventbus.EventBusClient
import indexer.Document
import indexer.Index
import indexer.Indexer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import search.ResultSearchSerializer
import search.Search
import server.indexer.AddDocumentToIndexSerializer
import server.indexer.IndexerCommand.Companion.ADD_DOCUMENT_TO_INDEX
import spark.kotlin.ignite

class RetroIndexApp(searchPort: Int, eventBusClient: EventBusClient) : App(eventBusClient) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(RetroIndexApp::class.java)
        const val RETRO_INDEX_CHANNEL = "retro_index"
    }

    private val index = Index()
    private val search = Search()
    private val http = ignite().port(searchPort)


    init {
        eventBusClient.addHandler("/event") {
            Utils.parseBody(request.body()) {
                when (it.type) {
                    ADD_DOCUMENT_TO_INDEX -> addDocument(
                        Gson().fromJson(
                            it.obj,
                            AddDocumentToIndexSerializer::class.java
                        )
                    )
                }
            }
        }
        http.get("/search") {
            val query = request.queryParams("q")
            Gson().toJson(search(query))
        }
        eventBusClient.subscribe(RETRO_INDEX_CHANNEL, eventBusClient.getCallBackURL("/event"))
    }


    override fun run() {
        LOGGER.info("RetroIndex '$uid' created")
    }

    private fun addDocument(addDocumentToIndexSerializer: AddDocumentToIndexSerializer) {
        index.addDocument(addDocumentToIndexSerializer.document)
        LOGGER.info("Add document with url ${addDocumentToIndexSerializer.document.URL}")
    }

    private fun search(query: String): ResultSearchSerializer {
        val page = Lexer.lex(query)
        val doc = Indexer().getDocument(page)
        val docs = hashSetOf<Document>()
        doc.metadata.keys.forEach { word ->
            index.retroIndex[word]?.let {
                docs.addAll(it)
            }
        }
        val listDocuments = search.searchQuery(doc, docs, index.documents.size.toLong())
        return ResultSearchSerializer(query, listDocuments)
    }
}