package search

import indexer.Document
import indexer.index
import java.util.*
import kotlin.math.log10
import kotlin.math.sqrt

class Search {

    fun normalizeVector(list: Vector<Double>): Vector<Double> {
        val magnitude = sqrt(list.map { d -> d * d }.sum())
        return Vector(list.map { d -> d / magnitude })
    }

    fun tfIdf(token: String, doc: Document): Double {
        val tf: Double = doc.metadata.get(token)?.frequency ?: return 0.0
        val numberDocument = index.documents
            .filter { doc -> doc.metadata.containsKey(token) }.size
        val idf = log10(index.documents.size.toDouble() / (1.0 + numberDocument.toDouble()))
        return tf * idf
    }

    fun searchQuery(tokens: List<String>) {
        var tfIdfVectors = HashMap<String, Vector<Double>>()
        for (token in tokens) {
            if (tfIdfVectors.containsKey(token))
                continue
            var tfIdfs = Vector<Double>()
            index.documents.forEach { doc -> tfIdfs.add(tfIdf(token, doc)) }
            tfIdfs = normalizeVector(tfIdfs)
            tfIdfVectors[token] = tfIdfs
        }
    }
}