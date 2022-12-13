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

class CalcWay(
    private val area: Area,
    private val starts: List<Point>,
    private val end: Point
) {

    private var minWay: Way? = null
    private var calcPoint = mutableMapOf<Point, Int>()

    fun calc(): Int {
//       val compareByLength: Comparator<Way> = compareBy { distance(it.point, end ) + it.path.size }
//        val ways = PriorityQueue(compareByLength)
        val ways = LinkedList<Way>()
        ways.addAll(starts.map { Way(it, mutableSetOf(it)) })

        while (ways.size > 0) {
            val way = ways.pollFirst()

            if (way.point == end) {
                if (minWay == null || way.path.size < minWay!!.path.size) {
                    minWay = way
                }
            } else {
                ways.addAll(getNext(way))
            }
        }
        return min()
    }

    private fun getNext(way: Way): Collection<Way> =
        area.getNeighbours(way.point.x, way.point.y)
            .asSequence()
            .filter { it.height - way.point.height < 2 }
            .filter { !way.path.contains(it) }
            .map { p ->
                val path = way.path.toMutableSet()
                path.add(p)
                Way(p, path)
            }
            .filter { checkPoint(it.point, it.path.size) }
            .toList()

    private fun checkPoint(point: Point, path: Int): Boolean {
        val size = calcPoint[point] ?: Int.MAX_VALUE
        if (size > path) {
            calcPoint[point] = path
            return true
        }
        return false
    }

    private fun distance(point1: Point, point2: Point): Int =
        ((point1.x - point2.x).toDouble().pow(2) + (point1.y - point2.y).toDouble().pow(2)).pow(0.5).toInt()

    private fun min(): Int = (minWay?.path?.size ?: Int.MAX_VALUE) - 1
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
    val path: MutableSet<Point>,
)

data class Point(
    val x: Int,
    val y: Int,
    val height: Int
)

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