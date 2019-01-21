package crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Lexer {
    fun getContent(url: String) {
        val doc = Jsoup.connect("http://en.wikipedia.org/").get()
    }

    fun removeTags(document: Document): String = document.text()
}