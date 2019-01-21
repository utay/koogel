package indexer

import crawler.Page
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Indexer {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Indexer::class.java)

        fun index(page: Page) {
            LOGGER.info("Indexing {}", page.URL)
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
}
