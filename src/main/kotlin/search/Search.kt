package search

import crawler.Lexer
import indexer.Document
import indexer.Indexer
import indexer.index
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.log10
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.collections.HashMap

class Search {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Search::class.java)
    }

    private fun tfIdf(token: String, doc: Document, docs: HashSet<Document>): Double {
        val tf: Double = doc.metadata[token]?.frequency ?: return 0.0
        val numberDocument = docs.filter { d -> d.metadata.containsKey(token) }.size
        if (numberDocument == 0)
            return 0.0
        val idf = abs(log10(index.documents.size.toDouble() / (numberDocument.toDouble())))
        return tf * idf
    }

    private fun cosSimularity(query: Vector<Double>, doc: Vector<Double>): Double {
        var dot = 0.0
        query.forEachIndexed { index, v ->
            dot += v * doc[index]
        }
        val queryNorm = Utils.magnitude(query)
        val docNorm = Utils.magnitude(doc)
        return if (queryNorm != 0.0 && docNorm != 0.0) dot / (queryNorm * docNorm) else 0.0
    }

    private fun similarity(query: Vector<Double>, docs: HashMap<Document, Vector<Double>>): List<Document> {
        var result = ArrayList<Pair<Document, Double>>()
        LOGGER.debug("TF-IDF for query - {}", query.toString())
        docs.forEach { document, vector ->
            result.add(Pair(document, cosSimularity(query, vector)))
        }
        LOGGER.debug("CosSimilarity for documents - {}", result.map { p -> Pair(p.first.URL, p.second) })
        return result.sortedByDescending { p -> p.second }.map { r -> r.first }
    }

    fun searchQuery(query: String): List<Document> {
        LOGGER.info("Initialize search for query '{}'", query)
        val page = Lexer.lex(query, "")
        val docQuery = Indexer.getDocument(page)
        val documentVectors = HashMap<Document, Vector<Double>>()
        val queryValue = Vector<Double>()
        val docs = HashSet<Document>()

        for (token in page.content) {
            val documents = index.retroIndex[token] ?: continue
            docs.addAll(documents)
        }

        if (docs.isEmpty()) {
            LOGGER.info("No results for query '{}'", query)
            return Collections.emptyList()
        }

        docs.forEach { doc -> documentVectors[doc] = Vector() }

        for (token in page.content) {
            queryValue.add(tfIdf(token, docQuery, docs))
            docs.forEach { doc ->
                documentVectors[doc]?.add(tfIdf(token, doc, docs))
            }
        }

        val resDocs = similarity(queryValue, documentVectors)
        LOGGER.debug("Results for query: '{}' -> {}", query, resDocs.map { d -> d.URL }.toString())
        return resDocs
    }
}