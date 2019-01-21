package indexer

import crawler.Page
import search.Search
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val index = Index()

fun main(args: Array<String>) {

    val indexer = Indexer()
    val page = Page("https://en.wikipedia.org/wiki/Main_Page")
    val page2 = Page("https://en.wikipedia.org/wiki/Tata")
    val page3 = Page("https://en.wikipedia.org/wiki/Jamshedpur")
    indexer.index(page)
    indexer.index(page2)
    indexer.index(page3)
    val s = Search()
    s.searchQuery(Arrays.asList("day"))
}