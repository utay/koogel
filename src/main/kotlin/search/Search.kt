package search

import indexer.Document
import indexer.Indexer
import indexer.index
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sqrt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.collections.HashMap

class Search {

    private val LOGGER: Logger = LoggerFactory.getLogger(Search::class.java)

    private fun magnitude(list: Vector<Double>): Double {
        return sqrt(list.map { d -> d * d }.sum())
    }

    private fun normalizeVector(list: Vector<Double>): Vector<Double> {
        val magnitude = magnitude(list)
        return if (magnitude == 0.0) list else Vector(list.map { d -> d / magnitude })
    }

    private fun tfIdf(token: String, doc: Document, docs: HashSet<Document>): Double {
        val tf: Double = doc.metadata[token]?.frequency ?: return 0.0
        val numberDocument = docs.filter { d -> d.metadata.containsKey(token) }.size
        val idf = abs(log10(index.documents.size.toDouble() / (numberDocument.toDouble())))
        return tf * idf
    }

    private fun cosSimularity(query: Vector<Double>, doc: Vector<Double>): Double {
        var dot: Double = 0.0
        query.forEachIndexed { index, v ->
            dot += v * doc[index]
        }
        val queryNorm = magnitude(query)
        val docNorm = magnitude(doc)
        return dot / (queryNorm * docNorm)
    }

    private fun simularity(query: Vector<Double>, docs: HashMap<Document, Vector<Double>>): List<Document> {
        var result = ArrayList<Pair<Document, Double>>()
        docs.forEach { document, vector ->
            result.add(Pair(document, cosSimularity(query, vector)))
        }
        return result.sortedByDescending { p -> p.second }.map { r -> r.first }
    }

    fun searchQuery(tokens: List<String>) {
        var documentVectors = HashMap<Document, Vector<Double>>()
        var docs = HashSet<Document>()

        for (token in tokens) {
            val documents = index.retroIndex[token] ?: continue
            docs.addAll(documents)
        }

        docs.forEach { doc -> documentVectors[doc] = Vector() }

        for (token in tokens) {
            docs.forEach { doc ->
                documentVectors[doc]?.add(tfIdf(token, doc, docs))
            }
        }
    }
}