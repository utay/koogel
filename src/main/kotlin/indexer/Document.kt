package indexer

data class Document(
    var URL: String?,
    var metadata: HashMap<String, Metadata>,
    var rawContent: String
)
