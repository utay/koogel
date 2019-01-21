package indexer

class Index {
    lateinit var documents: ArrayList<Document>

    fun addDocument(doc: Document) {
        documents.add(doc)
    }
}
