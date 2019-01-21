package crawler

import java.io.File

class Synonymer {

    companion object {
        private val synonymesFiles = arrayOf(
            "src/staticData/Thes_SynSimRel_a-b.csv",
            "src/staticData/Thes_SynSimRel_c-e.csv",
            "src/staticData/Thes_SynSimRel_f-j.csv",
            "src/staticData/Thes_SynSimRel_k-o.csv",
            "src/staticData/Thes_SynSimRel_p-r.csv",
            "src/staticData/Thes_SynSimRel_s-t.csv",
            "src/staticData/Thes_SynSimRel_u-z.csv"
        )
    }

    private var synonyms: HashMap<String, String> = hashMapOf()

    init {
        fillMap()
    }

    fun getSynonym(word: String): String = synonyms[word] ?: word


    private fun readFileAsLinesUsingBufferedReader(fileName: String): List<String> =
        File(fileName).bufferedReader().readLines()

    private fun fillMap() {
        for (filename in synonymesFiles) {
            val lines = readFileAsLinesUsingBufferedReader(filename)
            for (line in lines.slice(1 until lines.count())) {
                val tokens = line.split(",")
                for (i in 5 until tokens.count()) {
                    synonyms[Lexer.removePunctuationsAndSpaces(tokens[i])] =
                            Lexer.removePunctuationsAndSpaces(tokens[0])
                }
            }
        }
    }
}