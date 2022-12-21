package org.home

import java.time.Instant.now
import java.util.*


fun solution16() {

    val inputListTest = readInput("inputs/input16test.txt")
    val inputList = readInput("inputs/input16.txt")

    println("Solution 16:")
    println("   test ${part1(inputListTest) == 1651 && part2(inputListTest) == 1707}")
    println("   part 1 answer ${part1(inputList)}") // 2114
    var time = now().toEpochMilli()
    println("   part 2 answer ${part2(inputList)} time = ${now().toEpochMilli() - time} ms") //2666
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

class Volkan(
    private val distance: Map<Pair<Point, Point>, Int>
) {

    private val cache2 = mutableMapOf<Key, Int>()

    fun part1(
        n: Int,
        current: Point,
        ways: List<Point>,
    ): Int {
        if (n <= 0) return 0

        if (ways.isEmpty()) return current.rate * n

        val cacheVal = Key(n, current, ways.toSet())

        val max = cache2[cacheVal] ?: ways.map { next ->
            part1(
                n - distance[Pair(current, next)]!! - 1,
                next,
                ways.toList() - next
            )
        }.maxOf { it } + current.rate * n

        cache2[cacheVal] = max

        return max
    }


    data class Key(val i: Int, val current: Volkan.Point, val toSet: Set<Point>)

    fun part2(
        n: Int,
        first: Point,
        ratePoints: Set<Point>
    ): Int {
        val combinations = combinationsAll(ratePoints.toList())
        var i = -1
        val result = combinations
            .map {
                ++i
                //print("\r")
                //print("   combination ${(i.toDouble()/2 * 100).div(combinations.size.div(2)).toInt()}%")
                part1(n, first, it.toList()) + part1(n, first, (ratePoints - it).toList())
            }.maxOf { it }
        //println()
        return result
    }

    data class Point(
        val name: String,
        val rate: Int,
    )

}

fun <T> combinations(lst: List<T>, k: Int): List<List<T>> {
    if (k == 0) {
        return mutableListOf(mutableListOf())
    }
    val local = mutableListOf<List<T>>()

    for (idx in 0.rangeTo(lst.size - 1)) {
        for (item in combinations(lst.subList(idx + 1, lst.size), k - 1)) {
            local.add(mutableListOf(lst[idx]) + item)
        }
    }
    return local
}


fun <T> combinationsAll(lst: List<T>): List<List<T>> {
    val local = mutableListOf<List<T>>()
    for (k in 1.rangeTo(lst.size)) {
        local.addAll(combinations(lst, k))
    }
    return local
}

private fun part1(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })

    val ratePoints = points.filter { it.name != "AA" }.filter { it.rate > 0 }.toList()
    val way = points.flatMap { Dijkstra(it, edges).calc().map { kv -> Pair(it, kv.key) to kv.value } }.toMap()

    return Volkan(way).part1(
        30, points.first { it.name == "AA" }, ratePoints,
    )
}

private fun part2(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })

    val way = points.flatMap { Dijkstra(it, edges).calc().map { kv -> Pair(it, kv.key) to kv.value } }.toMap()
    val ratePoints = points.filter { it.name != "AA" }.filter { it.rate > 0 }.toList()

    return Volkan(way).part2(26, points.first { it.name == "AA" }, ratePoints.toSet())
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




