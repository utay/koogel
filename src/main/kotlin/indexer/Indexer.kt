package indexer

import crawler.Page

class Indexer {

    fun index(page: Page) {
        val doc = Document(page.URL, HashMap())

        for (word in page.content) {
            if (!doc.metadata.containsKey(word)) {
                val pageIndices = ArrayList<Int>();

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

        index.addDocument(doc)
    }
}
