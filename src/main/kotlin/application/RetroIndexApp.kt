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
import kotlin.math.max
import kotlin.math.min

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
            response.type("application/json")
            val query = request.queryParams("q")
            val limit = request.queryParams("limit")?.toInt() ?: 10
            val offset = request.queryParams("offset")?.toInt() ?: 0
            val searchResult = search(query, limit, offset)
            Gson().toJson(searchResult)
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

    private fun cleanSearchResult(docs: ArrayList<Document>, query: Document): ArrayList<Document> {
        docs.forEach {
            it.metadata = HashMap(it.metadata.filter { meta -> query.metadata.containsKey(meta.key) })
        }
        return docs
    }

    private fun search(query: String, limit: Int, offset: Int): ResultSearchSerializer {
        val page = Lexer.lex(query)
        val doc = Indexer().getDocument(page)
        val docs = hashSetOf<Document>()
        doc.metadata.keys.forEach { word ->
            index.retroIndex[word]?.let {
                docs.addAll(it)
            }
        }

        var listDocuments = search.searchQuery(doc, docs, index.documents.size.toLong())
        val total = listDocuments.size
        val min = min(offset, max(total - 1, 0))
        val max = min(min + limit, total)
        listDocuments = ArrayList(listDocuments.subList(min, max))
        return ResultSearchSerializer(query, cleanSearchResult(listDocuments, doc), total)
    }
}