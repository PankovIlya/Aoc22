package org.home


import java.lang.Integer.max
import java.lang.Integer.min


fun solution14() {

    val inputListTest = readInput("inputs/input14test.txt")
    val inputList = readInput("inputs/input14.txt")

    println("Solution 14:")
    println("   test ${part1(inputListTest) == 24 && part2(inputListTest) == 93}")
    println("   part 1 answer ${part1(inputList)}") //1001
    println("   part 2 answer ${part2(inputList)}") //27976
}

class SandMover(
    private var start: Int,
    private val yMax: Int,
    private var field: List<MutableList<Int>>,
    private var infinity : Boolean
) {
    private var count = 0
    fun move(): Int {

        var point = Point(start, 0, 0)

        while (point.value >= 0) {
            val nextPoint = nextSteps(point, getNextPoint(point))
            if (nextPoint != point) {
                point = nextPoint
            } else {
                break
            }
            if (nextPoint == Point(start, 0, 0)){
                count += 1
                //printField()
            }
        }
        //printField()
        return count + 1
    }

    private fun nextSteps(point: Point, next: List<Point>): Point {
        next.forEach { nextPoint ->
            if (nextPoint.value == -1 || nextPoint.value == 0) {
                return nextPoint
            }
        }
        field[point.y][point.x] = 2
        return Point(start, 0, 0)
    }

    private fun getNextPoint(point: Point): List<Point> {
        if (point.x - 1 < 0) {
            calcNewField()
            start += 1; point.x += 1
        }
        if (point.x + 1 >= field[0].size) {
            calcAddField()
        }
        if (point.y + 1 >= field.size) {
            return listOf(Point(-1, -1, -1))
        }

        return listOf(
            Point(point.x, point.y + 1, field[point.y + 1][point.x]),
            Point(point.x - 1, point.y + 1, field[point.y + 1][point.x - 1]),
            Point(point.x + 1, point.y + 1, field[point.y + 1][point.x + 1]),
        )
    }

    private fun calcAddField() =
        field.forEachIndexed { i, row ->
            row.add(if (i == yMax && infinity) 1 else 0)
        }

    private fun calcNewField(){
        val newField = mutableListOf<MutableList<Int>>()
        field.forEachIndexed { i, row ->
            newField.add((mutableListOf(if (i == yMax && infinity) 1 else 0) + row).toMutableList())
        }
        field = newField
    }

/*    private fun printField() {
        field.forEach {
            println(it.joinToString(" ") { v -> if (v == 0) "." else if (v == 1) "#" else "o" })
        }
    }*/


    data class Point(
        var x: Int,
        var y: Int,
        val value: Int,
    )
}


private fun part1(inputList: List<String>): Int {
    val (minX, maxX, maxY) = getMinMax(inputList)
    val field = getField1(minX, maxX, maxY, inputList)
    return SandMover(500 - minX, maxY, field, false).move()
}

private fun part2(inputList: List<String>): Int {
    val (minX, maxX, maxY) = getMinMax(inputList)
    val field = getField2(minX, maxX, maxY, inputList)
    return SandMover(500 - minX, maxY + 2, field, true).move()
}

private fun getField1(minX: Int, maxX: Int, maxY: Int, inputList: List<String>): List<MutableList<Int>> {
    val field = List(maxY + 1) { MutableList(maxX - minX + 1) { 0 } }
    calcNewField(field, inputList, minX)
    return field
}

private fun getField2(minX: Int, maxX: Int, maxY: Int, inputList: List<String>): MutableList<MutableList<Int>> {
    val field = MutableList(maxY + 2) { MutableList(maxX - minX + 1) { 0 } }
    field.add(MutableList(maxX - minX + 1) { 1 })
    calcNewField(field, inputList, minX)
    return field
}

fun calcNewField(field: List<MutableList<Int>>, inputList: List<String>, minX: Int) {
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



