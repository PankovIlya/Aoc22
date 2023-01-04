package org.home

import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt


fun main(args: Array<String>) {
    solution7()
    solution8()
    solution9()
    solution10()
    solution11()
    solution12()
    solution13()
    solution14()
    //solution15()
    solution16()
    solution18()
    solution21()
    solution23()
    solution24()
    solution25()
    solution19()
}

fun readInput(file: String): List<String> {
    val inputStream: InputStream = File(file).inputStream()
    return inputStream.bufferedReader().readLines()
}










