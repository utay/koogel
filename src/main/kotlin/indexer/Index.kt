package indexer

class Index {
    var documents: ArrayList<Document> = ArrayList()

    fun addDocument(doc: Document) {
        documents.add(doc)
    }
}
