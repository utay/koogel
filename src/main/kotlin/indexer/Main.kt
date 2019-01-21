package indexer

import crawler.Page
import search.Search
import java.util.*
import org.apache.log4j.BasicConfigurator

val index = Index()

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    val page = Page("https://en.wikipedia.org/wiki/Main_Page")
    val page2 = Page("https://en.wikipedia.org/wiki/Tata")
    val page3 = Page("https://en.wikipedia.org/wiki/Jamshedpur")
    Indexer.index(page)
    Indexer.index(page2)
    Indexer.index(page3)
    val s = Search()
    s.searchQuery(Arrays.asList("day"))
}