package org.home

import java.time.Instant.now
import java.util.*
import kotlin.math.pow


fun solution13() {

    val inputListTest = readInput("inputs/input13test.txt")
    val inputList = readInput("inputs/input13.txt")

    println("Solution 13:")
    println("   test ${part1(inputListTest) }")
    println("   part 1 answer ${part1(inputList)}")
    val time = now().toEpochMilli()
    println("   part 2 answer ${part2(inputList)} time = ${now().toEpochMilli() - time} ") //354
}


private fun part1(inputList: List<String>): Int {
    return 0
}

private fun part2(inputList: List<String>): Int {
    return 0
}

private fun parseInput(inputList: List<String>): List<List<String>> {

}
