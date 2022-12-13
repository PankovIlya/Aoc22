package org.home

import java.time.Instant.now
import java.util.*
import kotlin.math.abs


fun solution12() {

    val inputListTest = readInput("inputs/input12test.txt")
    val inputList = readInput("inputs/input12.txt")

    println("Solution 12:")
    println("   test ${part1(inputListTest) == 31 && part2(inputListTest) == 29}")
    println("   part 1 answer ${part1(inputList)}")
    var time = now().toEpochMilli()
    println("   part 2 BFS answer ${part2(inputList)} time = ${now().toEpochMilli() - time} ms") //354
    time = now().toEpochMilli()
    println("   part 2 Search A* answer ${part22(inputList)} time = ${now().toEpochMilli() - time} ms") //354
}


class BFS(
    area: Area,
    private val starts: List<Point>,
    private val end: Point
) : Searcher(area) {

    override fun calc(): Int {
        calcPoint = mutableMapOf()

        var minWay: Way? = null
        val ways = LinkedList<Way>()

        ways.addAll(starts.map { Way(it, mutableSetOf(it)) })

        while (ways.size > 0) {
            val way = ways.pollFirst()

            if (way.point == end) {
                minWay = way
                break
            } else {
                ways.addAll(getNext(way))
            }
        }
        return (minWay?.path?.size ?: Int.MAX_VALUE) - 1
    }
}


class SearchA(
    area: Area,
    private val starts: List<Point>,
    private val end: Point
) : Searcher(area) {

    override fun calc(): Int {
        var minWay: Way? = null

        calcPoint = mutableMapOf()

        val compareByLength: Comparator<Way> = compareBy { it.point.distance(end) + it.path.size }
        val ways = PriorityQueue(compareByLength)

        ways.addAll(starts.map { Way(it, mutableSetOf(it)) })

        while (ways.size > 0) {
            val way = ways.poll()

            if (way.point == end) {
                minWay = way
                break
            } else {
                ways.addAll(getNext(way))
            }
        }
        return (minWay?.path?.size ?: Int.MAX_VALUE) - 1
    }
}

abstract class Searcher(
    private val area: Area,
) {

    internal lateinit var calcPoint: MutableMap<Point, Int>

    abstract fun calc(): Int

    fun getNext(way: Way): Collection<Way> =
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

private fun Point.distance(point: Point): Int =
    abs(this.x - point.x) + abs(this.y - point.y)

private fun part1(inputList: List<String>): Int {
    val dict = getDict()
    val area = parseInput(inputList, dict)
    val start = parseInput('S', inputList, dict)
    val end = parseInput('E', inputList, dict)

    return SearchA(Area(area), listOf(start), end).calc()
}

private fun part2(inputList: List<String>): Int {
    val dict = getDict()
    val area = parseInput(inputList, dict)
    val start = area.flatMap { row -> row.filter { p -> p.height == 0 } }
    val end = parseInput('E', inputList, dict)

    return BFS(Area(area), start, end).calc()
}

private fun part22(inputList: List<String>): Int {
    val dict = getDict()
    val area = parseInput(inputList, dict)
    val start = area.flatMap { row -> row.filter { p -> p.height == 0 } }
    val end = parseInput('E', inputList, dict)

    return SearchA(Area(area), start, end).calc()
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