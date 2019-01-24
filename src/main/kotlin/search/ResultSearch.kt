package search

import indexer.Document

data class ResultSearch(val query: String, val documents: ArrayList<Document>)