package indexer

import crawler.Page
import java.util.*
import kotlin.collections.HashMap

class Indexer {
    fun index(page: Page) {
        val doc = Document(page.URL, HashMap())

        for (word in page.content) {
            if (doc.frequencies.containsKey(word)) {
                val frequency = Collections.frequency(page.content, word) / page.content.size.toDouble()
                doc.frequencies[word] = frequency
            }
        }

        index.addDocument(doc)
    }
}
