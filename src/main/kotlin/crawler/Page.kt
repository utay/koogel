package crawler

import crawler.Lexer.Companion.isStopWord
import crawler.Lexer.Companion.removePunctuationsAndSpaces
import org.jsoup.Jsoup

data class Page(
    var URL: String,
    var content: ArrayList<String> = arrayListOf(),
    var rawContent: String? = null,
    var textMap: HashMap<String, ArrayList<Int>> = hashMapOf()
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
            val cleanword = removePunctuationsAndSpaces(word)
            if (!isStopWord(cleanword)) {
                if (textMap.containsKey(cleanword)) {
                    textMap[cleanword]?.add(index)
                } else {
                    textMap[cleanword] = arrayListOf(index)
                }
                content.add(cleanword)
            }
            index = nextIndex
        }
    }
}
