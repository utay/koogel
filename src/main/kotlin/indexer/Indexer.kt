package indexer

import crawler.Page
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Indexer {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(Indexer::class.java)
    }

    fun getDocument(page: Page): Document {
        LOGGER.info("Indexing {}", page.URL)
        val doc = Document(page.URL, HashMap(), page.rawContent ?: "")

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
