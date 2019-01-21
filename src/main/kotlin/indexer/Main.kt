package indexer

import crawler.Crawler
import crawler.Lexer
import search.Search
import java.util.*
import org.apache.log4j.BasicConfigurator

val index = Index()

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val crawl = Crawler()
    val url = "https://en.wikipedia.org/wiki/Main_Page"
    val url2 = "https://en.wikipedia.org/wiki/Tata"
    val url3 = "https://en.wikipedia.org/wiki/Jamshedpur"
    val url4 = "https://www.duke-energy.com/home"
    Indexer.index(Lexer.lex(crawl.getContent(url).text(), url))
    Indexer.index(Lexer.lex(crawl.getContent(url2).text(), url2))
    Indexer.index(Lexer.lex(crawl.getContent(url3).text(), url3))
    Indexer.index(Lexer.lex(crawl.getContent(url4).text(), url4))
    val s = Search()
    s.searchQuery("company tata")
}