package crawler

import org.jsoup.Jsoup

class Lexer {
    fun getContent(url: String) {
        val doc = Jsoup.connect("http://en.wikipedia.org/").get()
    }
}