package indexer

class Index {

    var documents: ArrayList<Document> = ArrayList()
    var retroIndex: HashMap<String, ArrayList<Document>> = HashMap()

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
