package search

import java.util.*
import kotlin.math.sqrt

class Utils {
    companion object {

        fun magnitude(list: Vector<Double>): Double {
            return sqrt(list.map { d -> d * d }.sum())
        }

        fun normalizeVector(list: Vector<Double>): Vector<Double> {
            val magnitude = magnitude(list)
            return if (magnitude == 0.0) list else Vector(list.map { d -> d / magnitude })
        }
    }
}