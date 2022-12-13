package org.home

import kotlin.math.pow

fun solution9() {
    val inputListTest1 = readInput("inputs/input9test1.txt")
    val inputListTest2 = readInput("inputs/input9test2.txt")
    val inputList = readInput("inputs/input9.txt")

    println("Solution 9:")
    println("   test ${part1(inputListTest1) == 13 && part2(inputListTest2) == 36}")
    println("   part 1 answer ${part1(inputList)}")
    println("   part 2 answer ${part2(inputList)}")
}

private fun part2(inputList: List<String>): Int {
    val moveMaker = MoveMaker(MoveMaker.Point(0, 0), 1.rangeTo(9).map { MoveMaker.Point(0, 0) })
    inputList.forEach {
        moveMaker.move(it)
    }
    return moveMaker.getTailUniqueCount()
}


private fun part1(inputList: List<String>): Int {
    val moveMaker = MoveMaker()
    inputList.forEach {
        moveMaker.move(it)
    }
    return moveMaker.getTailUniqueCount()
}


private class MoveMaker(
    private val head: Point = Point(0, 0),
    private val tail: List<Point> = listOf(Point(0, 0))
) {
    companion object {
        const val CHECK_DISTANCE = 2.0
    }

    private val history = mutableSetOf<Point>()

    fun move(command: String) {
        val (direct, step) = command.split(" ")
        move(head, direct, step.toInt())
    }

    private fun move(point: Point, direct: String, step: Int) =
        repeat(step) {
            movePoint(point, direct)
            checkPoint()
            history.add(Point(tail.last().x, tail.last().y))
        }

    private fun checkPoint() {
        var prev = head
        tail.forEach {
            check(prev, it)
            prev = it
        }
    }

    private fun check(point1: Point, point2: Point) {
        if (distance(point1, point2) < CHECK_DISTANCE) {
            return
        }

        if (point1.x > point2.x) {
            movePoint(point2, "R")
        } else if (point1.x < point2.x) {
            movePoint(point2, "L")
        }
        if (point1.y > point2.y) {
            movePoint(point2, "U")
        } else if (point1.y < point2.y) {
            movePoint(point2, "D")
        }

    }

    private fun movePoint(point: Point, direct: String) =
        when (direct) {
            "R" -> point.x += 1
            "L" -> point.x -= 1
            "U" -> point.y += 1
            "D" -> point.y -= 1
            else -> println("Unknown direct = $direct")
        }

    private fun distance(point1: Point, point2: Point): Int =
        ((point1.x - point2.x).toDouble().pow(2) + (point1.y - point2.y).toDouble().pow(2)).pow(0.5).toInt()

    fun getTailUniqueCount() = history.size

    data class Point(
        var x: Int,
        var y: Int
    )
}

