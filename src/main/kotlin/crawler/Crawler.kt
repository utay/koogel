package crawler

import org.apache.log4j.BasicConfigurator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Crawler {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Crawler::class.java)
    }

    fun getContent(url: String) = Jsoup.connect(url).get()

    private fun crawlHrefs(document: Document, baseUrl: String): ArrayList<String> {
        val hrefs: Elements = document.select("a")
        val urls = arrayListOf<String>()
        hrefs.forEach {
            var href = it.attr("href")
            href = href.replace(Regex("\\?.*"), "").removeSuffix("/")
            if (!href.isEmpty() && !href.startsWith("#")) {
                var prefix = ""
                // for example "/wiki/help"
                if (!href.startsWith("http") && !href.startsWith("www")) {
                    var url = baseUrl
                    if (baseUrl.startsWith("https")) {
                        url = url.replaceFirst("https://", "")
                        prefix = "https://"
                    } else if (href.startsWith("http")) {
                        url = url.replaceFirst("http://", "")
                        prefix = "http://"
                    }
                    val domain = url.slice(0 until url.indexOf("/"))
                    href = "$prefix$domain$href"
                }
                urls.add(href)
            }
        }
        return urls
    }

    fun crawl(url: String): Pair<Page, ArrayList<String>>? {
        LOGGER.info("crawling $url")
        return try {
            val content = getContent(url)
            val page = Lexer.lex(content.text(), url)
            val urls = crawlHrefs(content, url)
            Pair(page, urls)
        } catch (e: Exception) {
            LOGGER.error("Error fetching url: $url", e.message)
            null
        }
    }

    fun main(args: Array<String>?) {
        BasicConfigurator.configure()
        val url = "https://en.wikipedia.org/wiki/Paris"
        crawl(url)
    }
}
