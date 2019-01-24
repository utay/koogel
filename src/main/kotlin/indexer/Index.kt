package indexer

import java.util.concurrent.ConcurrentHashMap

class Index {

    var documents: ArrayList<Document> = ArrayList()
    var retroIndex: ConcurrentHashMap<String, ArrayList<Document>> = ConcurrentHashMap()

    private fun addTermWithDocument(term: String, doc: Document) {
        if (retroIndex.containsKey(term)) {
            retroIndex[term]?.add(doc)
        } else {
            retroIndex[term] = ArrayList()
            retroIndex[term]?.add(doc)
        }
    }

    fun addDocument(doc: Document) {
        documents.add(doc)
        for (word in doc.metadata.keys) {
            addTermWithDocument(word, doc)
        }
    }
}
