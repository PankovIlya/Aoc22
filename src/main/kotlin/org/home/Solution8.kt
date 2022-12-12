package org.home

fun solution8() {
    val inputList = readInput("inputs/input8.txt")

    println("Solution 8 part 1 answer ${part1(inputList)}")
    println("Solution 8 part 2 answer ${part2(inputList)}")
}

private fun part2(inputList: List<String>): Int {
    val matrix = inputList.map { it.toCharArray().map { c -> c.toString() }.map { s -> s.toInt() } }
    var max = 0

    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            val current = calcView(i, j, matrix)
            if (current > max) {
                max = current
            }
        }
    }
    return max

}

fun calcView(i: Int, j: Int, matrix: List<List<Int>>): Int =
    calcLine(matrix[i], j) * calcLine(matrix.map { it[j] }, i)

fun calcLine(line: List<Int>, index: Int) =
    (index - calcMaxIndexLeft(line.subList(0, index), line[index])) *
            (calcMaxIndexRight(line.subList(index + 1, line.size), line[index]))


fun calcMaxIndexLeft(line: List<Int>, value: Int): Int {
    for (high in line.size - 1 downTo 0) {
        if (line[high] >= value) {
            return high
        }
    }
    return 0
}

fun calcMaxIndexRight(line: List<Int>, value: Int): Int {
    line.forEachIndexed { i, high ->
        if (high >= value) {
            return i + 1
        }
    }
    return line.size
}


private fun part1(inputList: List<String>): Int {
    val matrix = inputList.map { it.toCharArray().map { c -> c.toString() }.map { s -> s.toInt() } }
    var sum = 0

    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, value ->
            if (check(i, j, value, matrix)) {
                ++sum
            }
        }
    }
    return sum
}

fun check(i: Int, j: Int, value: Int, matrix: List<List<Int>>) =
    checkLine(matrix[i], j, value) || checkLine(matrix.map { it[j] }, i, value)

fun checkLine(line: List<Int>, index: Int, value: Int) =
    !checkMax(line, 0, index, value) || !checkMax(line, index + 1, line.size, value)

fun checkMax(line: List<Int>, i: Int, j: Int, value: Int) =
    line.subList(i, j).firstOrNull { it >= value }?.let { true } ?: false

