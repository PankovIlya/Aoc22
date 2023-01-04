package org.home

import java.util.*

fun solution24() {

    val inputListTest = readInput("inputs/input24test.txt")
    val inputList = readInput("inputs/input24.txt")

    println("Solution 24:")
    println("   test ${part1(inputListTest) == 18 && part2(inputListTest) == 54}")
    println("   part 1 answer ${part1(inputList)}") // 343
    println("   part 2 answer ${part2(inputList)}") // 960
}

private fun part1(inputList: List<String>): Int {
    val start = SearchPath.Point(parseInputPoint(inputList[0]), 0)
    val end = SearchPath.Point(parseInputPoint(inputList[inputList.size - 1]), inputList.size - 1)
    val storms = parseInput(inputList, start, end)

    return SearchPath(start, end, storms, 0).search()
}

private fun part2(inputList: List<String>): Int {
    val start = SearchPath.Point(parseInputPoint(inputList[0]), 0)
    val end = SearchPath.Point(parseInputPoint(inputList[inputList.size - 1]), inputList.size - 1)
    val storms = parseInput(inputList, start, end)

    val step1 = SearchPath(start, end, storms, 0).search()
    val step2 = SearchPath(end, start, storms, step1).search()
    return SearchPath(start, end, storms, step2).search()
}


class SearchPath(
    private val start: Point,
    private val end: Point,
    private val storms: MutableMap<Int, Set<Point>>,
    private val step: Int
) {

    fun search(): Int {
        val visited = mutableSetOf<SnowWay>()
        var max = Int.MAX_VALUE
        val queue = LinkedList<SnowWay>()
        queue.add(SnowWay(start, step))

        while (queue.size > 0) {
            val way = queue.pollFirst()

            if (way.point == end) {
                if (way.step < max) max = way.step
            } else {
                val next = way.next()
                    .filter { !visited.contains(it) }
                    .filter {
                        val set = storms[(it.step - 1) % storms.size]!!
                        !set.contains(it.point)
                    }
                    .filter { it.step < max }
                visited.addAll(next)
                queue.addAll(next)
            }
        }
        return max
    }

    data class Point(
        val x: Int,
        val y: Int
    )
}

data class SnowWay(
    val point: SearchPath.Point,
    val step: Int
) {
    fun next(): List<SnowWay> =
        listOf(
            SnowWay(SearchPath.Point(point.x, point.y), step + 1),
            SnowWay(SearchPath.Point(point.x, point.y - 1), step + 1),
            SnowWay(SearchPath.Point(point.x - 1, point.y), step + 1),
            SnowWay(SearchPath.Point(point.x, point.y + 1), step + 1),
            SnowWay(SearchPath.Point(point.x + 1, point.y), step + 1),
        )
}

data class Storm(
    val point: SearchPath.Point,
    val fooX: ((Int) -> Int)? = null,
    val fooY: ((Int) -> Int)? = null,
) {
    fun move() =
        Storm(
            SearchPath.Point(
                fooX?.invoke(point.x) ?: point.x,
                fooY?.invoke(point.y) ?: point.y,
            ),
            fooX,
            fooY
        )
}

private fun parseInput(
    inputList: List<String>,
    start: SearchPath.Point,
    end: SearchPath.Point
): MutableMap<Int, Set<SearchPath.Point>> {
    val yMin = 0
    val yMax = inputList.size - 1
    val xMin = 0
    val xMax = inputList[0].length - 1
    var storms = mutableListOf<Storm>()

    inputList.forEachIndexed { i, str ->
        str.forEachIndexed { j, c ->
            val value = when (c) {
                '#' -> Storm(SearchPath.Point(j, i), { x -> x }) { y -> y }
                '>' -> Storm(SearchPath.Point(j, i), { x -> if (x + 1 == xMax) xMin + 1 else x + 1 }) { y -> y }
                '<' -> Storm(SearchPath.Point(j, i), { x -> if (x - 1 == xMin) xMax - 1 else x - 1 }) { y -> y }
                '^' -> Storm(SearchPath.Point(j, i), { x -> x }) { y -> if (y - 1 == yMin) yMax - 1 else y - 1 }
                'v' -> Storm(SearchPath.Point(j, i), { x -> x }) { y -> if (y + 1 == yMax) yMin + 1 else y + 1 }
                else -> null
            }
            value?.let { storms.add(it) }
        }
    }

    storms.add(Storm(SearchPath.Point(start.x, start.y - 1), { it }, { it }))
    storms.add(Storm(SearchPath.Point(end.x, end.y + 1), { it }, { it }))

    val setStorms = mutableSetOf<Set<Storm>>()
    val mapStorms = mutableMapOf<Int, Set<SearchPath.Point>>()

    0.rangeTo(inputList.size * inputList[0].length).forEach { i ->
        storms = storms.map { it.move() }.toMutableList()
        val count = setStorms.size
        setStorms.add(storms.toSet())
        if (count < setStorms.size) {
            mapStorms[i] = storms.map { it.point }.toSet()
        }
    }

    return mapStorms

}

fun parseInputPoint(s: String): Int {
    s.forEachIndexed { index, c -> if (c == '.') return index }
    return 0
}






