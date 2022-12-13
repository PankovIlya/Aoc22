package org.home

import java.time.Instant.now
import java.util.*
import kotlin.math.pow


fun solution12() {

    val inputListTest = readInput("inputs/input121.txt")
    val inputList = readInput("inputs/input12.txt")

    println("Solution 12:")
    println("   test ${part1(inputListTest) == 31 && part2(inputListTest) == 29}")
    println("   part 1 answer ${part1(inputList)}")
    val time = now().toEpochMilli()
    println("   part 2 answer ${part2(inputList)} time = ${now().toEpochMilli() - time} ") //354
}

private fun part1(inputList: List<String>): Int {
    val dict = getDict()
    val area = parseInput(inputList, dict)
    val start = parseInput('S', inputList, dict)
    val end = parseInput('E', inputList, dict)

    return CalcWay(Area(area), listOf(start), end).calc()
}

private fun part2(inputList: List<String>, print: Boolean = false): Int {
    val dict = getDict()
    val area = parseInput(inputList, dict)
    val start = area.flatMap { row -> row.filter { p -> p.height == 0 } }
    val end = parseInput('E', inputList, dict)

    return CalcWay(Area(area), start, end).calc()
}

private fun parseInput(inputList: List<String>, dict: Map<Char, Int>): List<List<Point>> {
    val resultLis = mutableListOf<MutableList<Point>>()
    inputList.forEachIndexed { i, row ->
        resultLis.add(mutableListOf())
        row.toCharArray().forEachIndexed { j, c ->
            resultLis.last().add(Point(j, i, dict[c]!!))
        }
    }
    return resultLis
}


private fun parseInput(value: Char, inputList: List<String>, dict: Map<Char, Int>): Point {
    inputList.forEachIndexed { i, row ->
        row.toCharArray().forEachIndexed { j, c ->
            if (c == value) {
                return Point(j, i, dict[value]!!)
            }
        }
    }
    return Point(0, 0, 0)
}

private fun getDict(): Map<Char, Int> {
    val dict = mutableMapOf<Char, Int>()
    'a'.rangeTo('z').forEachIndexed { i, c -> dict[c] = i }

    dict['S'] = dict['a']!!
    dict['E'] = dict['z']!!

    return dict
}


class CalcWay(
    private val area: Area,
    private val starts: List<Point>,
    private val end: Point
) {

    private var minWay: Way? = null
    private var calcPoint = mutableMapOf<Point, Int>()

    fun calc(): Int {
        val compareByLength: Comparator<Way> = compareBy { it.steps.size }
        val ways = PriorityQueue(compareByLength)
        ways.addAll(starts.map { Way(it, mutableSetOf(it)) })

        while (ways.size > 0) {
            val way = ways.remove()
            if (way.point == end) {
                if (minWay == null || way.steps.size < minWay!!.steps.size) {
                    minWay = way
                }
            } else {
                ways.addAll(getNext(way))
            }
        }
        return min() - 1

    }

    private fun min(): Int = minWay?.steps?.size ?: Int.MAX_VALUE

    private fun getNext(way: Way): Collection<Way> =
        area.getNeighbours(way.point.x, way.point.y)
            .asSequence()
            .filter { !way.steps.contains(it) }
            .map { p ->
                val steps = way.steps.toMutableSet()
                steps.add(p)
                Way(p, steps)
            }
            .filter { checkPoint(it.point, it.steps.size) }
            .toList()


    private fun checkPoint(point: Point, path: Int): Boolean {
        val size = calcPoint[point] ?: Int.MAX_VALUE
        if (size > path) {
            calcPoint[point] = path
            return true
        }
        return false
    }
}


class Area(
    private val map: List<List<Point>>
) {
    fun getNeighbours(x: Int, y: Int): List<Point> {
        val points = mutableListOf<Point>()
        if (x > 0) {
            points.add(map[y][x - 1])
        }
        if (x < map[0].size - 1) {
            points.add(map[y][x + 1])
        }
        if (y > 0) {
            points.add(map[y - 1][x])
        }
        if (y < map.size - 1) {
            points.add(map[y + 1][x])
        }
        return points
    }
}

data class Way(
    val point: Point,
    val steps: MutableSet<Point>
)

data class Point(
    val x: Int,
    val y: Int,
    val height: Int
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