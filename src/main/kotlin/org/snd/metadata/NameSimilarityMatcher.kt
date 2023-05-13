package org.snd.metadata

import org.apache.commons.text.similarity.LevenshteinDistance
import org.snd.metadata.model.NameMatchingMode
import org.snd.metadata.model.NameMatchingMode.CLOSEST_MATCH
import org.snd.metadata.model.NameMatchingMode.EXACT

class NameSimilarityMatcher private constructor(
    private val mode: NameMatchingMode
) {
    private val levenshteinDistance = LevenshteinDistance.getDefaultInstance()

    fun matches(name: String, namesToMatch: Collection<String>): Boolean {
        val cleanName = getCleanName(name)
        return namesToMatch.map { matches(cleanName, it) }.any { it }
    }

    fun matches(name: String, nameToMatch: String): Boolean {
        return if (mode == EXACT || name.length in 1..3) name == nameToMatch
        else {
            val distance = levenshteinDistance.apply(name.uppercase(), nameToMatch.uppercase())
            val distanceThreshold = when (name.length) {
                in 4..6 -> 1
                in 7..9 -> 2
                else -> 3
            }
            return distance <= distanceThreshold
        }
    }

    fun getCleanName(name: String): String {
        val cleanName = name.replace(" Edition", "Edition").split(" ").filter { it != "Omnibus" && !it.endsWith("Edition") }.joinToString(" ")
        return cleanName
    }

    companion object {
        private val EXACT_MATCHER: NameSimilarityMatcher = NameSimilarityMatcher(EXACT)
        private val CLOSEST_MATCH_MATCHER: NameSimilarityMatcher = NameSimilarityMatcher(CLOSEST_MATCH)

        fun getInstance(mode: NameMatchingMode): NameSimilarityMatcher {
            return when (mode) {
                EXACT -> EXACT_MATCHER
                CLOSEST_MATCH -> CLOSEST_MATCH_MATCHER
            }
        }
    }
}
