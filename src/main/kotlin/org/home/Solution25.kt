package org.home

import kotlin.math.pow

fun solution25() {

    val inputListTest = readInput("inputs/input25test.txt")
    val inputList = readInput("inputs/input25.txt")

    println("Solution 25:")
    println("   test ${part1(inputListTest) == "2=-1=0" && part2(inputListTest) == ""}")
    println("   part 1 answer ${part1(inputList)}") // 2-1-110-=01-1-0-0==2
    println("   part 2 answer ${part2(inputList)}") // 960
}

private fun part1(inputList: List<String>): String {
    return inputList.sumOf { snafuToLong(it) }.toSnafu()
}

private fun part2(inputList: List<String>): String {
    return ""
}

fun Long.toSnafu(): String {
    var result = ""
    var number = this
    while (number > 0) {
        var remainder = 0
        val module = (number % 5)
        if (module == 4L) {
            result += "-"
            remainder = 1
        } else if (module == 3L) {
            result += "="
            remainder = 1
        } else {
            result += module.toString()
        }
        number = (number / 5) + remainder
    }
    return result.reversed()
}

fun snafuToLong(s: String): Long {
    var result = 0.0
    s.forEachIndexed { index, c ->
        //10
        result += 5.0.pow(s.length - index - 1) * parseSnafu(c)
    }
    return result.toLong()
}

fun parseSnafu(c: Char): Int =
    when (c) {
        '1' -> 1
        '2' -> 2
        '-' -> -1
        '=' -> -2
        else -> 0
    }






