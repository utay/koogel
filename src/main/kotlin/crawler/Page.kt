package crawler

data class Page(
    var URL: String? = null,
    var content: ArrayList<String> = arrayListOf(),
    var rawContent: String? = null,
    var rawIndices: HashMap<String, ArrayList<Int>> = hashMapOf()
)
