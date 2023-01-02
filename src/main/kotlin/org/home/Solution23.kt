package org.home

import java.util.*


fun solution23() {

    val inputListTest = readInput("inputs/input23test.txt")
    val inputList = readInput("inputs/input23.txt")

    println("Solution 23:")
    println("   test ${part1(inputListTest) == 110 && part2(inputListTest) == 20}")
    println("   part 1 answer ${part1(inputList)}") // 4218
    println("   part 2 answer ${part2(inputList)}") // 976
}

private fun part1(inputList: List<String>): Int {
    var points = parseInput(inputList)
    val rounds = Rounds()

    1.rangeTo(10).forEach { _ ->
        val map = mutableMapOf<Point, List<Point>>()

        points.forEach { point ->
            val nextPoint = rounds.getNextPoint(point, points)
            map[nextPoint.first] = (map[nextPoint.first] ?: listOf()) + nextPoint.first
        }
        points =
            map.flatMap { kv -> if (kv.value.size == 1) listOf(kv.key) else kv.value.map { it.prev ?: it } }.toSet()
        rounds.next()
    }

    var result = 0

    points.minOf { it.x }.rangeTo(points.maxOf { it.x }).forEach { x ->
        points.minOf { it.y }.rangeTo(points.maxOf { it.y }).forEach { y ->
            if (!points.contains(Point(x, y))) {
                result += 1
            }
        }
    }
    return result
}


class Rounds {

    private val steps = LinkedList<Round>()

    init {
        steps.add(
            Round(
                { point, points ->
                    points.contains(Point(point.x, point.y - 1)) ||
                            points.contains(Point(point.x - 1, point.y - 1)) ||
                            points.contains(Point(point.x + 1, point.y - 1))
                },
                { Point(it.x, it.y - 1, it) })
        )

        steps.add(
            Round(
                { point, points ->
                    points.contains(Point(point.x, point.y + 1)) ||
                            points.contains(Point(point.x + 1, point.y + 1)) ||
                            points.contains(Point(point.x - 1, point.y + 1))
                },
                { Point(it.x, it.y + 1, it) })
        )
        steps.add(
            Round(
                { point, points ->
                    points.contains(Point(point.x - 1, point.y)) ||
                            points.contains(Point(point.x - 1, point.y + 1)) ||
                            points.contains(Point(point.x - 1, point.y - 1))
                },
                { Point(it.x - 1, it.y, it) })
        )
        steps.add(
            Round(
                { point, points ->
                    points.contains(Point(point.x + 1, point.y)) ||
                            points.contains(Point(point.x + 1, point.y + 1)) ||
                            points.contains(Point(point.x + 1, point.y - 1))
                },
                { Point(it.x + 1, it.y, it) })
        )
    }

    fun getNextPoint(point: Point, points: Set<Point>): Pair<Point, Boolean> {
        var res = true
        steps.forEach { step ->
            if (step.check(point, points)) {
                res = false
            }
        }

        if (res) return Pair(point, true)

        steps.forEach { step ->
            if (!step.check(point, points)) {
                return Pair(step.next(point), false)
            }
        }
        return Pair(point, false)
    }

    fun next() {
        val first = steps.pollFirst()
        steps.add(first)
    }
}

class Round(
    val check: (Point, Set<Point>) -> Boolean,
    val next: (Point) -> Point
)

data class Point(
    val x: Int,
    val y: Int,
    val prev: Point? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

private fun parseInput(inputList: List<String>): Set<Point> {
    val points = mutableSetOf<Point>()
    inputList.forEachIndexed { i, str ->
        str.forEachIndexed { j, char ->
            if (char == '#') {
                points.add(Point(j, i))
            }
        }
    }
    return points
}

private fun part2(inputList: List<String>): Int {
    var points = parseInput(inputList)
    val rounds = Rounds()
    var noSteps = false
    var round = 0

    while (!noSteps) {
        val map = mutableMapOf<Point, List<Point>>()
        noSteps = true
        points.forEach { point ->
            val nextPoint = rounds.getNextPoint(point, points)
            map[nextPoint.first] = (map[nextPoint.first] ?: listOf()) + nextPoint.first
            noSteps = noSteps && nextPoint.second
        }
        points =
            map.flatMap { kv -> if (kv.value.size == 1) listOf(kv.key) else kv.value.map { it.prev ?: it } }.toSet()
        rounds.next()
        round += 1
    }


    return round
}



