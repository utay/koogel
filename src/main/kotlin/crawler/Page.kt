package crawler

data class Page(
    var URL: String,
    var content: List<String>,
    var rawContent: String,
    var textMap: Map<String, List<Int>>
)
