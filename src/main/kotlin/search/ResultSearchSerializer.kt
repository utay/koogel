package search

import indexer.Document

data class ResultSearchSerializer(val query: String, val documents: ArrayList<Document>, val totalResults: Int)