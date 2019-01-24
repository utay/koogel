package application

import com.mashape.unirest.http.Unirest
import indexer.Document
import org.slf4j.LoggerFactory
import search.ResultSearchSerializer
import search.Search
import spark.kotlin.Http
import spark.kotlin.ignite
import com.google.gson.Gson




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

    private fun searchQuery(query: String): ResultSearchSerializer {
        LOGGER.info("Query for '$query'")
        //GET Information from retroIndex
        val response = Unirest.get(indexUri).asJson()
        val infos = Gson().fromJson(response.body.toString(), search.ResultInformationIndexSerializer::class.java)

        val listDocuments = search.searchQuery(query, HashSet(infos.documents), infos.docSize)
        return ResultSearchSerializer(query, listDocuments as ArrayList<Document>)
    }
}