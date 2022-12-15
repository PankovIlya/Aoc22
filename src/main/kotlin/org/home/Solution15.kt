package org.home


import java.lang.Long.max
import java.lang.Long.min
import kotlin.math.abs


fun solution15() {

    val inputListTest = readInput("inputs/input15test.txt")
    val inputList = readInput("inputs/input15.txt")

    println("Solution 15:")
    println("   test ${part2(inputListTest, 20) == 0L}")
   // println("   test ${part1(inputListTest, 10) == 26L && part2(inputListTest, 20) == 0L}")
    //println("   part 1 answer ${part1(inputList, 2000000)}") //5508234
    // println("   part 2 answer ${part2(inputList, 4000000)}") //27976
}

class Coverage(
    private val listSB: List<Pair<Point, Point>>
) {

    private lateinit var lineCoverage: MutableMap<Long, MutableList<Pair<Long, Long>>>

    fun calc() {

        lineCoverage = mutableMapOf()

        listSB.forEach {
            calcCoverage(it.first, it.second)
        }
    }

    fun calcLine(line : Long) = lineCoverage[line]?.sumOf { it.second - it.first } ?: 0L

    fun calcFreeLine(x : Long) : Long {
        lineCoverage.keys.sorted().forEach {
            println("$it ${lineCoverage[it]}")
        }
        return 0
    }

    private fun calcCoverage(sensor: Point, beacon: Point) {
        val distance = sensor.distance(beacon)

        for (y in (sensor.y - distance).rangeTo(sensor.y + distance)) {

            val coverageX = distance - abs(sensor.y - y)

            lineCoverage[y] = addCoverage(
                Pair(sensor.x - coverageX, sensor.x + coverageX),
                lineCoverage[y]
            )
        }
    }

    private fun addCoverage(
        new: Pair<Long, Long>,
        current: MutableList<Pair<Long, Long>>?
    ): MutableList<Pair<Long, Long>> {
        current ?: return mutableListOf(new)

        val newIntervals = mutableListOf<Pair<Long, Long>>()
        var first = new

        current.forEach { second ->
            val result = checkCoverage(first, second)
            first = result[0]
            if (result.size == 2) {
                newIntervals.add(result[1])
            }
        }
        newIntervals.add(first)

        return newIntervals
    }

    private fun checkCoverage(interval1: Pair<Long, Long>, interval2: Pair<Long, Long>): List<Pair<Long, Long>> {

       if (interval1.first <= interval2.first && interval1.second >= interval2.second){
           return listOf(interval1)
       }
       if (interval1.first >= interval2.first && interval1.second <= interval2.second){
            return listOf(interval2)
       }


        if (interval1.second < interval2.first || interval2.second < interval1.first){
            return listOf(interval2, interval1)
        }

        return listOf(Pair(min(interval1.first, interval2.first) , max(interval1.second, interval2.second)))
    }

    data class Point(
        val x: Long,
        val y: Long,
    )
}

private fun Coverage.Point.distance(point: Coverage.Point): Long =
    abs(this.x - point.x) + abs(this.y - point.y)


private fun parseInput(inputList: List<String>): List<Pair<Coverage.Point, Coverage.Point>> =
    inputList.map {
        val command = it.split(" ")
        Pair(
            Coverage.Point(
                command[2].split("=")[1].split(",")[0].toLong(),
                command[3].split("=")[1].split(":")[0].toLong()
            ),
            Coverage.Point(
                command[8].split("=")[1].split(",")[0].toLong(),
                command[9].split("=")[1].toLong()
            )
        )
    }

private fun part1(inputList: List<String>, line : Long): Long {
    val list = parseInput(inputList)
    val coverage = Coverage(list)
    coverage.calc()
    return coverage.calcLine(line)
}

private fun part2(inputList: List<String>, wide : Long): Long {
    val list = parseInput(inputList)
    val coverage = Coverage(list)
    coverage.calc()
    return coverage.calcFreeLine(wide)
}




