package crawler

import org.jsoup.Jsoup

class Crawler {

    private fun getContent(url: String) = Jsoup.connect(url).get().text()

    fun main(args: Array<String>?) {
        val url = "https://en.wikipedia.org/wiki/Paris"
        val content = getContent(url)
        val page = Lexer.lex(content, url)
    }
}
