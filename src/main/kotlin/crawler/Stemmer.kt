package crawler

import java.util.regex.Pattern

class Stemmer {
    private val VOWEL = "(?:[aeiou]|(?<![aeiou])y)"
    private val CONSONANT = "(?:[bcdfghjklmnpqrstvwxz]|(?<=[aeiou])y|^y)"
    private val CONSONANT_CVC = "[bcdfghjklmnpqrstvz]"
    private val REDUCED_CONSONANT = "(?:[bcdfghjkmnpqrtvwx]|(?<=[aeiou])y|^y)"

    private val VOWELS_REGEX = Pattern.compile("$VOWEL+")
    private val M_REGEX = Pattern.compile("($VOWEL+$CONSONANT+)")

    fun stem(word: String): String {
        if (word.length < 3) {
            return word
        }
        var word1 = word
        word1 = step1(word1)
        word1 = step2(word1)
        word1 = step3(word1)
        word1 = step4(word1)
        return step5(word1)
    }

    private fun step1(word: String) = step1c(step1b(step1a(word)))

    private fun step1a(word: String) = when {
        word.endsWith("sses") -> word.replaceEnd("sses", "ss")
        word.endsWith("ies") -> word.withoutPostfix("es")
        word.endsWith("ss") -> word
        word.endsWith("s") -> word.withoutPostfix("s")
        else -> word
    }

    private fun step1b(word: String): String {

        var word1 = word

        if (word1.endsWith("eed")) {
            if (m(word1.withoutPostfix("eed")) > 0) {
                word1 = word1.withoutPostfix(1)
            }
        } else {
            word1 = when {
                word1.endsWith("ed") && word1.withoutPostfix("ed").containsVowel() -> word1.withoutPostfix("ed")
                word1.endsWith("ing") && word1.withoutPostfix("ing").containsVowel() -> word1.withoutPostfix("ing")
                else -> return word1
            }
            word1 = when {
                word1.endsWith("at") -> "${word1}e"
                word1.endsWith("bl") -> "${word1}e"
                word1.endsWith("iz") -> "${word1}e"
                word1.endsWithDoubleChars() && word1.endsWithPattern(REDUCED_CONSONANT) -> word1.withoutPostfix(1)
                word1.endsWithCvc() && m(word1) == 1 -> "${word1}e"
                else -> word1
            }
        }
        return word1
    }

    private fun step1c(word: String): String {
        return if (word.endsWith("y") && word.withoutPostfix("y").containsVowel()) word.replaceEnd("y", "i") else word
    }

    private fun step2(word: String) = when {
        word.endsWith("ational") -> word.replaceEnd("ational", "ate", 0)
        word.endsWith("tional") -> word.replaceEnd("tional", "tion", 0)
        word.endsWith("enci") -> word.replaceEnd("enci", "ence", 0)
        word.endsWith("anci") -> word.replaceEnd("anci", "ance", 0)
        word.endsWith("izer") -> word.replaceEnd("izer", "ize", 0)
        word.endsWith("bli") -> word.replaceEnd("bli", "ble", 0)
        word.endsWith("alli") -> word.replaceEnd("alli", "al", 0)
        word.endsWith("entli") -> word.replaceEnd("entli", "ent", 0)
        word.endsWith("eli") -> word.replaceEnd("eli", "e", 0)
        word.endsWith("ousli") -> word.replaceEnd("ousli", "ous", 0)
        word.endsWith("ization") -> word.replaceEnd("ization", "ize", 0)
        word.endsWith("ation") -> word.replaceEnd("ation", "ate", 0)
        word.endsWith("ator") -> word.replaceEnd("ator", "ate", 0)
        word.endsWith("alism") -> word.replaceEnd("alism", "al", 0)
        word.endsWith("iveness") -> word.replaceEnd("iveness", "ive", 0)
        word.endsWith("fulness") -> word.replaceEnd("fulness", "ful", 0)
        word.endsWith("ousness") -> word.replaceEnd("ousness", "ous", 0)
        word.endsWith("aliti") -> word.replaceEnd("aliti", "al", 0)
        word.endsWith("iviti") -> word.replaceEnd("iviti", "ive", 0)
        word.endsWith("biliti") -> word.replaceEnd("biliti", "ble", 0)
        word.endsWith("logi") -> word.replaceEnd("logi", "log", 0)
        else -> word
    }

    private fun step3(word: String) = when {
        word.endsWith("icate") -> word.replaceEnd("icate", "ic", 0)
        word.endsWith("ative") -> word.withoutPostfix("ative", 0)
        word.endsWith("alize") -> word.replaceEnd("alize", "al", 0)
        word.endsWith("iciti") -> word.replaceEnd("iciti", "ic", 0)
        word.endsWith("ical") -> word.replaceEnd("ical", "ic", 0)
        word.endsWith("ful") -> word.withoutPostfix("ful", 0)
        word.endsWith("ness") -> word.withoutPostfix("ness", 0)
        else -> word
    }

    private fun step4(word: String) = when {
        word.endsWith("al") -> word.withoutPostfix("al", 1)
        word.endsWith("ance") -> word.withoutPostfix("ance", 1)
        word.endsWith("ence") -> word.withoutPostfix("ence", 1)
        word.endsWith("er") -> word.withoutPostfix("er", 1)
        word.endsWith("ic") -> word.withoutPostfix("ic", 1)
        word.endsWith("able") -> word.withoutPostfix("able", 1)
        word.endsWith("ible") -> word.withoutPostfix("ible", 1)
        word.endsWith("ant") -> word.withoutPostfix("ant", 1)
        word.endsWith("ement") -> word.withoutPostfix("ement", 1)
        word.endsWith("ment") -> word.withoutPostfix("ment", 1)
        word.endsWith("ent") -> word.withoutPostfix("ent", 1)
        word.endsWith("ion")
                && (word.endsWith("tion") || word.endsWith("sion")) -> word.withoutPostfix("ion", 1)
        word.endsWith("ou") -> word.withoutPostfix("ou", 1)
        word.endsWith("ism") -> word.withoutPostfix("ism", 1)
        word.endsWith("ate") -> word.withoutPostfix("ate", 1)
        word.endsWith("iti") -> word.withoutPostfix("iti", 1)
        word.endsWith("ous") -> word.withoutPostfix("ous", 1)
        word.endsWith("ive") -> word.withoutPostfix("ive", 1)
        word.endsWith("ize") -> word.withoutPostfix("ize", 1)
        else -> word
    }

    private fun step5(word: String) = step5b(step5a(word))

    private fun step5a(word: String): String {
        if (word.endsWith("e")) {
            val wordWithoutE = word.withoutPostfix("e")
            val m = m(wordWithoutE)
            if (m > 1 || (m == 1 && !wordWithoutE.endsWithCvc())) {
                return wordWithoutE
            }
        }
        return word
    }

    private fun step5b(word: String) = if (word.endsWith("ll") && m(word) > 1) word.withoutPostfix("l") else word

    private fun m(word: String): Int {
        var result = 0
        val wordWithoutTrailingConsonants = word.replaceFirst("^$CONSONANT+", "")
        val matcher = M_REGEX!!.matcher(wordWithoutTrailingConsonants)
        while (matcher!!.find()) {
            result++
        }
        return result
    }

    private fun String.replaceEnd(pattern: String, replacement: String, requiredM: Int = -1): String {
        return if (requiredM < 0 || m(this.withoutPostfix(pattern)) > requiredM)
            this.replaceFirst("$pattern$", replacement)
        else this
    }

    private fun String.withoutPostfix(postfix: String, requiredM: Int = -1): String =
        this.withoutPostfix(postfix.length, requiredM)

    private fun String.withoutPostfix(postfixLength: Int, requiredM: Int = -1): String {
        val modifiedWord = this.substring(0, this.length - postfixLength)
        return if (requiredM < 0 || m(modifiedWord) > requiredM) modifiedWord else this
    }

    private fun String.endsWithDoubleChars(): Boolean =
        this.length > 1 && this[this.length - 1] == this[this.length - 2]

    private fun String.endsWithPattern(pattern: String): Boolean = Pattern.compile("$pattern$")!!.matcher(this)!!.find()
    private fun String.endsWithCvc(): Boolean = this.endsWithPattern("$CONSONANT$VOWEL$CONSONANT_CVC")
    private fun String.containsVowel(): Boolean = VOWELS_REGEX!!.matcher(this)!!.find()
}