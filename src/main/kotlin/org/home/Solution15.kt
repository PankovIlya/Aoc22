package org.home


import java.lang.Long.max
import java.lang.Long.min
import kotlin.math.abs


fun solution15() {

    val inputListTest = readInput("inputs/input15test.txt")
    val inputList = readInput("inputs/input15.txt")

    println("Solution 15:")
    val testCoverage = partMain(inputListTest)
    println("   test ${testCoverage.calcLine(10) == 26L && testCoverage.calcFreeLine(20) == 56000011L}")
    val coverage = partMain(inputList)
    println("   part 1 answer ${coverage.calcLine(2000000)}") //5508234
    println("   part 2 answer ${coverage.calcFreeLine(4000000)}") //10457634860779
}

class Coverage(
    private val listSB: List<Pair<Point, Point>>
) {

    companion object{
        private const val CONST = 4000000L
    }

    private lateinit var lineCoverage: MutableMap<Long, MutableList<Pair<Long, Long>>>

    fun calc() {

        lineCoverage = mutableMapOf()

        listSB.forEach {
            calcCoverage(it.first, it.second)
        }
    }

    fun calcLine(line : Long) = lineCoverage[line]?.sumOf { it.second - it.first } ?: 0L

    fun calcFreeLine(x : Long) : Long {
        lineCoverage.keys.filter { it in 0..x }.forEach {
            val intervals = lineCoverage[it]
            if (intervals != null && intervals.size > 1) {
                val newIntervals = addCoverage(Pair(0, x), intervals)
                if (newIntervals.size < intervals.size) {
                    return getGap(Pair(0L, x), intervals) * CONST + it
                }
            }
        }
        return 0
    }

    private fun getGap(pair: Pair<Long, Long>, intervals: MutableList<Pair<Long, Long>>): Long {
        val check = intervals.filter {it.second > pair.first && it.first < pair.second}.sortedBy { it.first }
        println(check)
        return check[0].second + 1
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

        if ((interval1.first < interval2.first && interval1.second + 1 >= interval2.first) ||
            (interval2.second + 1 >= interval1.first && interval2.first < interval1.first ) ){
            return listOf(Pair(min(interval1.first, interval2.first) , max(interval1.second, interval2.second)))
        }

         return listOf(interval1, interval2)
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

private fun partMain(inputList: List<String>): Coverage {
    val list = parseInput(inputList)
    val coverage = Coverage(list)
    coverage.calc()
    return coverage
}




