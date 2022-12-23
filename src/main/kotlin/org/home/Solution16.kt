package org.home

import java.util.*
import kotlin.math.max


fun solution16() {

    val inputListTest = readInput("inputs/input16test.txt")
    val inputList = readInput("inputs/input16.txt")

    println("Solution 16:")
    println("   test ${part1(inputListTest) == 1651 && part2(inputListTest) == 1707}")
    println("   part 1 answer ${part1(inputList)}") // 2114
    println("   part 2 answer ${part2(inputList)}") //2666
}

class Volkan(
    private val distance: Map<Pair<Point, Point>, Int>
) {

    fun part1(
        n: Int,
        first: Point,
        ratePoints: List<Point>,
    ) = allWay(n, first, ratePoints, listOf())
        .map { calcWay(n, first, it) }
        .maxOf { it }

    fun part2(
        n: Int,
        first: Point,
        ratePoints: List<Point>
    ): Int {
        val combinations = allWay(n, first, ratePoints, listOf())
            .asSequence()
            .map { it.toSet() to calcWay(n, first, it) }
            .groupingBy{it.first }.fold(0) { max, value -> max(max, value.second)  }
            .map {it.key to it.value }
            .sortedByDescending { it.second }
            .filter { it.second > 0 }
            .toList()

        var max = 0

        combinations.forEachIndexed { i, firstWay ->
            if (firstWay.second * 2 < max) return max
            for (j in (i + 1) until combinations.size) {
                val second = combinations[j]

                if ((firstWay.first intersect second.first).isEmpty()) {
                    val value = firstWay.second + second.second
                    if (value > max) {
                        max = value
                    }
                }

            }
        }
        return max

    }

    private fun allWay(
        n: Int,
        current: Point,
        ways: List<Point>,
        done: List<Point>
    ): List<List<Point>> {
        if (n > 0) {
            return listOf(done) + ways.flatMap { next ->
                allWay(
                    n - distance[Pair(current, next)]!! - 1,
                    next,
                    ways - next,
                    done + next
                )
            }

        }
        return listOf()
    }

    private fun calcWay(n: Int, first: Point, list: List<Point>): Int {
        var prev = first
        var time = n
        var sum = 0
        list.forEach { next ->
            time -= distance[Pair(prev, next)]!! + 1
            sum += next.rate * time
            prev = next
        }
        return sum
    }

    data class Point(
        val name: String,
        val rate: Int,
    )

}

class Dijkstra(
    private val main: Volkan.Point,
    private val edges: Map<Volkan.Point, List<Volkan.Point>>
) {
    fun calc(): MutableMap<Volkan.Point, Int> {

        val marked = mutableSetOf<Volkan.Point>()
        val points = mutableMapOf<Volkan.Point, Int>()

        val compareByLength: Comparator<Volkan.Point> = compareBy { points[it] ?: Int.MAX_VALUE }
        val ways = PriorityQueue(compareByLength)
        ways.add(main)
        points[main] = 0

        while (!ways.isEmpty()) {
            val current = ways.remove()
            if (marked.contains(current)) continue
            edges[current]?.forEach { next ->
                if (points[next] ?: Int.MAX_VALUE > points[current]!! + 1) {
                    points[next] = points[current]!! + 1
                    ways.add(next)
                }
            }
        }
        return points
    }

}

private fun part1(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })

    val ratePoints = points
        .filter { it.name != "AA" }
        .filter { it.rate > 0 }
        .toList()

    val way = points
        .flatMap {
            Dijkstra(it, edges).calc()
                .map { kv -> Pair(it, kv.key) to kv.value }
        }
        .toMap()

    return Volkan(way).part1(30, points.first { it.name == "AA" }, ratePoints)
}

private fun part2(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })

    val way = points
        .flatMap {
            Dijkstra(it, edges).calc()
                .map { kv -> Pair(it, kv.key) to kv.value }
        }
        .toMap()

    val ratePoints = points
        .filter { it.name != "AA" }.filter { it.rate > 0 }
        .toList()

    return Volkan(way).part2(26, points.first { it.name == "AA" }, ratePoints)
}

fun parseInputEdges(inputList: List<String>, points: Map<String, Volkan.Point>) =
    inputList.associate {
        val arr = it.split("; ")

        val lst = arr[1].substring(if (arr[1].startsWith("tunnels lead to valves")) 23 else 22)
            .split(", ")

        points[arr[0].split(" ")[1]]!! to lst.map { p -> points[p]!! }

    }

fun parseInputPoint(inputList: List<String>) =
    inputList.map {
        val arr = it.split("; ")[0].split(" ")
        Volkan.Point(
            arr[1],
            arr[4].split("=")[1].toInt()
        )
    }




