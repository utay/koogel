package indexer

import crawler.Page
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Indexer {

    fun index(page: Page) {
        index.addDocument(getDocument(page))
    }

    fun getDocument(page: Page): Document {
         val doc = Document(page.URL, HashMap())

        for (word in page.content) {
            if (!doc.metadata.containsKey(word)) {
                val pageIndices = ArrayList<Int>()

                for ((index, word2) in page.content.withIndex()) {
                    if (word == word2) {
                        pageIndices.add(index)
                    }
                }

                doc.metadata[word] = Metadata(
                    pageIndices.size / page.content.size.toDouble(),
                    pageIndices,
                    page.rawIndices[word]!!
                )
            }
        }

        return doc
    }
}
