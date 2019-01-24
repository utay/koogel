package application

import indexer.Document
import org.slf4j.LoggerFactory
import search.ResultSearch
import spark.kotlin.Http
import spark.kotlin.ignite
import search.Search


class SearchApp(val port: Int, val indexUri: String) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchApp::class.java)
    }

    private val http: Http = ignite().port(5000)
    private val search = Search()

    /*init {
        http.get("/search") {
            val query = request.params("q")
            searchQuery(query)
        }
    }*/

    /*private fun searchQuery(query: String): ResultSearch {
        LOGGER.info("Query for '$query'")
        val listDocuments = search.searchQuery(query).map { d -> Document(d.URL, d.metadata.filter {  }) }
        return ResultSearch(query, listDocuments as ArrayList<Document>)
    }*/
}