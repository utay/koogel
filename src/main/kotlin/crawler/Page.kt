package crawler

import crawler.Lexer.Companion.isStopWord
import crawler.Lexer.Companion.removePunctuationsAndSpaces
import org.jsoup.Jsoup

data class Page(
    var URL: String,
    var content: ArrayList<String> = arrayListOf(),
    var rawContent: String? = null,
    var rawIndices: HashMap<String, ArrayList<Int>> = hashMapOf()
) {

    init {
        getContent()
        fillPage()
    }

    private fun getContent() {
        rawContent = Jsoup.connect(URL).get().text()
    }

    private fun fillPage() {
        if (rawContent == null) {
            println("Error: rawContent should not be null")
            return
        }

        val splitedText = rawContent!!.split(" ")
        var index = 0
        var nextIndex: Int
        for (word in splitedText) {
            nextIndex = index + word.count() + 1
            val cleanWord = removePunctuationsAndSpaces(word)
            if (!isStopWord(cleanWord)) {
                if (rawIndices.containsKey(cleanWord)) {
                    rawIndices[cleanWord]?.add(index)
                } else {
                    rawIndices[cleanWord] = arrayListOf(index)
                }
                content.add(cleanWord)
            }
            index = nextIndex
        }
    }
}
