package org.home

import java.util.*


fun solution18() {

    val inputListTest = readInput("inputs/input18test.txt")
    val inputList = readInput("inputs/input18.txt")

    println("Solution 18:")
    println("   test ${part1(inputListTest) == 64 && part2(inputListTest) == 58}")
    println("   part 1 answer ${part1(inputList)}") // 3636
    println("   part 2 answer ${part2(inputList)}") // 2102
}

private fun part1(inputList: List<String>): Int =
    calcSquare(parseInput(inputList))

private fun part2(inputList: List<String>): Int {
    val points = parseInput(inputList)
    val minMAx = getMaxMin(points)
    return calcSquare(calcDrowning(points, getAllPoints(minMAx)))
}

fun calcSquare(points: Set<Point3d>) =
    points.map { currentPoint -> getSiblingPoint(currentPoint, points)}
        .map { 6 - it.size }
        .sumOf { it }


fun getSiblingPoint(point: Point3d, points: Set<Point3d>, ) =
    getSibling(point).filter { points.contains(it) }.toSet()


fun calcDrowning(points: Set<Point3d>, allPoints: Set<Point3d>): Set<Point3d> {
    val ways = LinkedList<Point3d>()
    val visitors = mutableSetOf<Point3d>()
    ways.add(allPoints.first())
    visitors.add(allPoints.first())
    while (!ways.isEmpty()) {
        val point = ways.pollFirst()
        val nextPoints = getSibling(point)
            .filter { allPoints.contains(it) && !points.contains(it) && !visitors.contains(it)}
        visitors.addAll(nextPoints)
        ways.addAll(nextPoints)
    }
    return points + (allPoints - visitors)
}

fun getAllPoints(minMAx: List<Int>): Set<Point3d> =
    minMAx[0].rangeTo(minMAx[1]).flatMap{ x ->
        minMAx[2].rangeTo(minMAx[3]).flatMap { y ->
            minMAx[4].rangeTo(minMAx[5]).map { z ->
                Point3d(x, y, z)
            }
        }
    }.toSet()


fun getSibling(point: Point3d) =
    listOf(
        Point3d(point.x-1, point.y, point.z),
        Point3d(point.x+1, point.y, point.z),
        Point3d(point.x, point.y-1, point.z),
        Point3d(point.x, point.y+1, point.z),
        Point3d(point.x, point.y, point.z-1),
        Point3d(point.x, point.y, point.z+1),
    )

fun getMaxMin(points: Set<Point3d>) =
    listOf(
        points.map { it.x }.minOf { it } - 1,
        points.map { it.x }.maxOf { it } + 1,
        points.map { it.y }.minOf { it } - 1,
        points.map { it.y }.maxOf { it } + 1,
        points.map { it.z }.minOf { it } - 1,
        points.map { it.z }.maxOf { it } + 1,
    )


private fun parseInput(inputList: List<String>) =
    inputList.map {
        val (x, y, z) = it.split(",")
        Point3d(x.toInt(), y.toInt(), z.toInt())
    }.toSet()

data class Point3d(
    val x : Int,
    val y : Int,
    val z : Int,
)
