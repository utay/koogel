package application

import indexer.Document
import indexer.Metadata
import org.slf4j.LoggerFactory
import search.ResultSearch
import search.Search
import spark.kotlin.Http
import spark.kotlin.ignite


class SearchApp(val port: Int, val indexUri: String) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchApp::class.java)
    }

    private val http: Http = ignite().port(port)
    private val search = Search()

    init {
        http.get("/search") {
            val query = request.params("q")
            searchQuery(query)
        }
    }

    private fun searchQuery(query: String): ResultSearch {
        LOGGER.info("Query for '$query'")
        val listDocuments = search.searchQuery(query)
        return ResultSearch(query, listDocuments as ArrayList<Document>)
    }
}