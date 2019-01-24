package search

import indexer.Document

data class ResultInformationIndexSerializer(val docSize: Long, val documents: ArrayList<Document>)