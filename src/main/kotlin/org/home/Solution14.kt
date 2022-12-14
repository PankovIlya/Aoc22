package org.home


import java.lang.Integer.max
import java.lang.Integer.min


fun solution14() {

    val inputListTest = readInput("inputs/input14test.txt")
    val inputList = readInput("inputs/input14.txt")

    println("Solution 14:")
    println("   test ${part1(inputListTest) == 24 && part2(inputListTest) == 93}")
    println("   part 1 answer ${part1(inputList)}")
    println("   part 2 answer ${part2(inputList)}") //354
}

class SandMover(
    private val x: Int,
    private val y: Int,
    private val field: List<MutableList<Int>>
) {
    private var count = 0
    fun move(): Int {

        var point = Point(x, 0, 0)

        while (point.value >= 0) {
            val next = getNextPoint(point)
            point = nextSteps(point, next)
        }
        return count
    }

    private fun nextSteps(point: Point, next: List<Point>): Point {
        next.forEach { nextPoint ->
            if (nextPoint.value == -1 || nextPoint.value == 0) {
                return nextPoint
            }
        }
        count += 1
        field[point.y][point.x] = 2
        return Point(x, 0, 0)
    }

    private fun getNextPoint(point: Point): List<Point> =
        if (point.y + 1 >= field.size || point.x - 1 < 0 || point.x + 1 >= field[0].size) {
            listOf(Point(-1, -1, -1))
        } else {
            listOf(
                Point(point.x, point.y + 1, field[point.y + 1][point.x]),
                Point(point.x - 1, point.y + 1, field[point.y + 1][point.x - 1]),
                Point(point.x + 1, point.y + 1, field[point.y + 1][point.x + 1]),
            )
        }

    data class Point(
        val x: Int,
        val y: Int,
        val value: Int,
    )
}

class SandMover2(
    private var x: Int,
    private val y: Int,
    private var field: List<MutableList<Int>>
) {
    private var count = 0
    fun move(): Int {

        var point = Point(x, 0, 0)

        while (point.value >= 0) {
            val next = getNextPoint(point)
            val nextPoint = nextSteps(point, next)
            if (nextPoint != point) {
                point = nextPoint
                if ((point) == Point(x, 0, 0)){
                    count += 1
                }
            } else {
                break
            }
        }
        printField()
        return count
    }

    private fun nextSteps(point: Point, next: List<Point>): Point {
        next.forEach { nextPoint ->
            if (nextPoint.value == -1 || nextPoint.value == 0) {
                return nextPoint
            }
        }
        field[point.y][point.x] = 2
        return Point(x, 0, 0)
    }

    private fun getNextPoint(point: Point): List<Point> {
        var add = 0
        if (point.x - 1 < 0) {
            val newField = mutableListOf<MutableList<Int>>()
            field.forEachIndexed { i, row ->
                newField.add((mutableListOf(if (i == y) 1 else 0) + row).toMutableList())
            }
            field = newField
            x += 1
            add += 1
        }
        if (point.x + 1 >= field[0].size) {
            field.forEachIndexed { i, row ->
                row.add(if (i == y) 1 else 0)
            }
        }
        if (point.y + 1 >= field.size) {
            return listOf(Point(-1, -1, -1))
        }

        return listOf(
            Point(point.x + add, point.y + 1, field[point.y + 1][point.x + add]),
            Point(point.x - 1 + add, point.y + 1, field[point.y + 1][point.x - 1 + add]),
            Point(point.x + 1 + add, point.y + 1, field[point.y + 1][point.x + 1 + add]),
        )
    }

    fun printField() {
        field.forEach {
            println(it.joinToString(" ") { v -> if (v == 0) "." else if (v == 1) "#" else "o" })
        }
    }


    data class Point(
        val x: Int,
        val y: Int,
        val value: Int,
    )
}


private fun part1(inputList: List<String>): Int {
    val (minX, maxX, maxY) = getMinMax(inputList)
    val field = List(maxY + 1) { MutableList(maxX - minX + 1) { 0 } }
    calcField(field, inputList, minX)
    val count = SandMover(500 - minX, maxY, field).move()
    return count
}

private fun part2(inputList: List<String>): Int {
    val (minX, maxX, maxY) = getMinMax(inputList)
    val field = MutableList(maxY + 2) { MutableList(maxX - minX + 1) { 0 } }
    field.add(MutableList(maxX - minX + 1) { 1 })
    calcField(field, inputList, minX)
    val count = SandMover2(500 - minX, maxY + 2, field).move()
    return count
}




fun calcField(field: List<MutableList<Int>>, inputList: List<String>, minX: Int) {
    inputList.forEach { s ->
        val iterator = s.split(" -> ").iterator()
        var (x1, y1) = iterator.next().split(",").map { it.toInt() }
        while (iterator.hasNext()) {
            val (x2, y2) = iterator.next().split(",").map { it.toInt() }
            if (x1 == x2) {
                val i = min(y1, y2)
                val j = max(y1, y2)
                for (y in i.rangeTo(j)) {
                    field[y][x1 - minX] = 1
                }
            }
            if (y1 == y2) {
                val i = min(x1, x2)
                val j = max(x1, x2)
                for (x in i.rangeTo(j)) {
                    field[y1][x - minX] = 1
                }
            }
            x1 = x2; y1 = y2
        }
    }
}


fun getMinMax(inputList: List<String>): List<Int> {
    var (minX, maxX, maxY) = listOf(Int.MAX_VALUE, 0, 0)

    inputList.forEach { s ->
        s.split(" -> ").forEach { value ->
            val (x, y) = value.split(",").map { it.toInt() }
            if (x > maxX) {
                maxX = x
            }
            if (x < minX) {
                minX = x
            }
            if (y > maxY) {
                maxY = y
            }
        }
    }
    return listOf(minX, maxX, maxY)
}



