package crawler

data class Page(
    var URL: String,
    var content: List<String>,
    var rawContent: String,
    var rawIndices: HashMap<String, ArrayList<Int>>
)
