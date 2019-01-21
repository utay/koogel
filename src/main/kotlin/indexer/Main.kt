package indexer

import crawler.Page
import java.util.*

val index = Index()

fun main(args: Array<String>) {
    val indexer = Indexer()
    indexer.index(Page("http://google.com",  Arrays.asList("blue", "red", "blue")))
    println(index.documents)
}