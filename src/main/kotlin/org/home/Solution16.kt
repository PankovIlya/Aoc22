package org.home

import java.time.Instant.now
import java.util.*


fun solution16() {

    val inputListTest = readInput("inputs/input16test.txt")
    val inputList = readInput("inputs/input16.txt")

    println("Solution 16:")
    println("   test ${part1(inputListTest) == 1651 && part2(inputListTest) == 1707}")
    var time = now().toEpochMilli()
    println("   part 1 answer ${part1(inputList)} time = ${now().toEpochMilli() - time}") // 2114
    time = now().toEpochMilli()
    println("   part 2 answer ${part2(inputList)} time = ${now().toEpochMilli() - time} ms") //2666
}


class Dijkstra(
    private val main: Point,
    private val edges: Map<Point, List<Point>>
) {
    fun calc(): MutableMap<Point, Int> {

        val marked = mutableSetOf<Point>()
        val points = mutableMapOf<Point, Int>()

        val compareByLength: Comparator<Point> = compareBy { points[it] ?: Int.MAX_VALUE }
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

    data class Point(
        val name: String,
        val rate: Int,
    )
}

class Volkan {


    private val cache2 = mutableMapOf<Key, Int>()

    fun part1(
        n: Int,
        current: Dijkstra.Point,
        ways: List<Dijkstra.Point>,
        distance: Map<Pair<Dijkstra.Point, Dijkstra.Point>, Int>
    ): Int {
        if (n <= 0) return 0
        if (ways.isEmpty()) return current.rate * n

        val cacheVal = Key(n, current, ways.toSet())
        val max = cache2[cacheVal]

        if (max != null){
            return max + current.rate * n
        }


        val result = ways.map { next ->
            part1(
                n - distance[Pair(current, next)]!! - 1,
                next,
                ways.toList() - next,
                distance
            )
        }.maxOf { it }

        cache2[cacheVal] = result

        return result + current.rate * n
        //cache[Pair<1, set {2,3}>] = max + cost(1) * n
    }


    data class Key(val i: Int, val current: Dijkstra.Point, val toSet: Set<Dijkstra.Point>)


    fun part2(
        first: Dijkstra.Point,
        combinations: List<List<Dijkstra.Point>>,
        ratePoints: Set<Dijkstra.Point>,
        way: Map<Pair<Dijkstra.Point, Dijkstra.Point>, Int>
    ): Int {
        println(combinations.size)
        println()
        var i = 0
        return combinations.map {
            print("\r")
            print(i)
            i += 1

            val set1 = it//.toSet()
            val set2 = ratePoints - set1

            part1(26, first, set1, way) + part1(26, first,  set2.toList(), way)

        }.maxOf { it }
    }

}

fun <T> combinations(lst: List<T>, k: Int): List<List<T>> {
    if (k == 0) {
        return mutableListOf(mutableListOf())
    }
    val local = mutableListOf<List<T>>()

    for (idx in 0.rangeTo(lst.size - 1 )) {
        for (item in combinations(lst.subList(idx + 1, lst.size), k - 1)) {
            local.add(mutableListOf(lst[idx]) + item)
        }
    }
    return local
}


fun <T> combinationsAll(lst: List<T>): List<List<T>> {
    val local = mutableListOf<List<T>>()
    for (k in 1.rangeTo(lst.size)){
        local.addAll(combinations(lst, k))
    }
    return local
}

private fun part1(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })
    val way = points.flatMap { Dijkstra(it, edges).calc().map { kv -> Pair(it, kv.key) to kv.value } }.toMap()
    return Volkan().part1(30,
        points.first { it.name == "AA" }, points.filter { it.name != "AA" }.filter { it.rate > 0 }, way
    )
}

private fun part2(inputList: List<String>): Int {
    val points = parseInputPoint(inputList)
    val edges = parseInputEdges(inputList, points.associateBy { it.name })
    val way = points.flatMap { Dijkstra(it, edges).calc().map { kv -> Pair(it, kv.key) to kv.value } }.toMap()
    val ratePoints = points.filter { it.name != "AA" }.filter { it.rate > 0 }.toList()
    val combinations = combinationsAll( ratePoints)
    return Volkan().part2( points.first { it.name == "AA" }, combinations, ratePoints.toSet(), way)
}

fun parseInputEdges(inputList: List<String>, points: Map<String, Dijkstra.Point>) =
    inputList.associate {
        val arr = it.split("; ")

        val lst = arr[1].substring(if (arr[1].startsWith("tunnels lead to valves")) 23 else 22)
            .split(", ")

        points[arr[0].split(" ")[1]]!! to lst.map { p -> points[p]!! }

    }

fun parseInputPoint(inputList: List<String>) =
    inputList.map {
        val arr = it.split("; ")[0].split(" ")
        Dijkstra.Point(
            arr[1],
            arr[4].split("=")[1].toInt()
        )
    }




