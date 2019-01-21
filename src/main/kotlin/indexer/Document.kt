package indexer

import kotlin.collections.HashMap

data class Document(
    var URL: String?,
    var metadata: HashMap<String, Metadata>
)
