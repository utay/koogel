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

class Search {

    private val LOGGER: Logger = LoggerFactory.getLogger(Search::class.java)

    private fun magnitude(list: Vector<Double>): Double {
        return sqrt(list.map { d -> d * d }.sum())
    }

    private fun normalizeVector(list: Vector<Double>): Vector<Double> {
        val magnitude = magnitude(list)
        return if (magnitude == 0.0) list else Vector(list.map { d -> d / magnitude })
    }

    private fun tfIdf(token: String, doc: Document): Double {
        val tf: Double = doc.metadata[token]?.frequency ?: return 0.0
        val numberDocument = index.documents
            .filter { doc -> doc.metadata.containsKey(token) }.size
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

    private fun simularity(query: Vector<Double>, docs: ArrayList<Vector<Double>>): List<Pair<Int, Double>> {
        var result = Vector<Pair<Int, Double>>()
        docs.forEachIndexed { index, vector ->
            result.add(Pair(index, cosSimularity(query, vector)))
        }
        return result.sortedByDescending { p -> p.second }
    }

    fun searchQuery(tokens: List<String>) {
        var tokensSet = HashSet<String>()
        var documentVectors = ArrayList<Vector<Double>>()
        index.documents.forEach { documentVectors.add(Vector<Double>()) }

        for (token in tokens) {
            if (tokensSet.contains(token))
                continue
            tokensSet.add(token)
            index.documents.forEachIndexed { index, doc ->  documentVectors[index].add(tfIdf(token, doc)) }
            documentVectors = ArrayList(documentVectors.map { a -> normalizeVector(a) })
        }
        LOGGER.info(String.format("searchQuery: %s", documentVectors.toString()))
    }
}