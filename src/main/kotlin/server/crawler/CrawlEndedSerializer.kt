package server.crawler

data class CrawlEndedSerializer(val urls: ArrayList<String>, val url: String, val id: String)