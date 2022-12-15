package org.home



import java.lang.Integer.max
import java.lang.Integer.min
import java.time.Instant
import kotlin.math.abs


fun solution15() {

    val inputListTest = readInput("inputs/input15test.txt")
    val inputList = readInput("inputs/input15.txt")

    println("Solution 15:")
    
    val testCoverage = partMain(inputListTest)
    println("   test ${testCoverage.calcLine(10) == 26 && testCoverage.calcFreeLine(20) == 56000011L}")

    val time = Instant.now().epochSecond
    val coverage = partMain(inputList)

    println("   execution time = ${Instant.now().epochSecond - time}s")
    println("   part 1 answer ${coverage.calcLine(2000000)}") //5508234
    println("   part 2 answer ${coverage.calcFreeLine(4000000)}") //10457634860779
}

class Coverage(
    private val listSB: List<Pair<Point, Point>>
) {

    companion object{
        private const val CONST = 4000000
    }

    private lateinit var lineCoverage: MutableMap<Int, MutableList<Pair<Int, Int>>>

    fun calc() {

        lineCoverage = mutableMapOf()

        listSB.forEach {
            calcCoverage(it.first, it.second)
        }
    }

    fun calcLine(line : Int) = lineCoverage[line]?.sumOf { it.second - it.first } ?: 0

    fun calcFreeLine(x : Int) : Long {
        lineCoverage.keys.filter { it in 0..x }.forEach {

            val intervals = lineCoverage[it]

            if (intervals != null && intervals.size > 1) {
                val newIntervals = addCoverage(Pair(0, x), intervals)

                if (newIntervals.size < intervals.size) {
                    return (intervals.sortedBy { it.first }[0].second + 1).toLong() * CONST + it
                }
            }
        }
        return 0
    }


    private fun calcCoverage(sensor: Point, beacon: Point) {
        val distance = sensor.distance(beacon)

        for (y in max((sensor.y - distance), 0) .rangeTo(min(sensor.y + distance, CONST))) {

            val coverageX = distance - abs(sensor.y - y)

            lineCoverage[y] = addCoverage(
                Pair(sensor.x - coverageX, sensor.x + coverageX),
                lineCoverage[y]
            )
        }
    }

    private fun addCoverage(
        new: Pair<Int, Int>,
        current: MutableList<Pair<Int, Int>>?
    ): MutableList<Pair<Int, Int>> {
        current ?: return mutableListOf(new)

        val newIntervals = mutableListOf<Pair<Int, Int>>()
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

    private fun checkCoverage(interval1: Pair<Int, Int>, interval2: Pair<Int, Int>): List<Pair<Int, Int>> {

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
        val x: Int,
        val y: Int,
    )
}

private fun Coverage.Point.distance(point: Coverage.Point): Int =
    abs(this.x - point.x) + abs(this.y - point.y)


private fun parseInput(inputList: List<String>): List<Pair<Coverage.Point, Coverage.Point>> =
    inputList.map {
        val command = it.split(" ")
        Pair(
            Coverage.Point(
                command[2].split("=")[1].split(",")[0].toInt(),
                command[3].split("=")[1].split(":")[0].toInt()
            ),
            Coverage.Point(
                command[8].split("=")[1].split(",")[0].toInt(),
                command[9].split("=")[1].toInt()
            )
        )
    }

private fun partMain(inputList: List<String>): Coverage {
    val list = parseInput(inputList)
    val coverage = Coverage(list)
    coverage.calc()
    return coverage
}




