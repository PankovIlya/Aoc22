package org.home


import java.lang.Integer.max
import java.lang.Integer.min


fun solution15() {

    val inputListTest = readInput("inputs/input15test.txt")
    val inputList = readInput("inputs/input15.txt")

    println("Solution 15:")
    println("   test ${part1(inputListTest) == 26L && part2(inputListTest) == 0}")
    //println("   part 1 answer ${part1(inputList)}") //1001
   //println("   part 2 answer ${part2(inputList)}") //27976
}

data class Point(
    val x : Long,
    val y : Long,
)

private fun part1(inputList: List<String>): Long {
    val list = parseInput(inputList)
    return Сoverage(list).calc(10L)
}

class Сoverage(
    private val listSB : List<Pair<Point, Point>>
)
{
    fun calc(line: Long): Long {



        listSB.forEach {
            calcСoverage(it.first, it.second)
        }
        return 0L
    }

    private fun calcСoverage(sensor: Point, beacon: Point) {

    }

}


private fun parseInput(inputList: List<String>): List<Pair<Point, Point>> =
    inputList.map { val command = it.split(" ")
                   Pair(
                       Point( command[2].split("=")[1].split(",")[0].toLong(),
                              command[3].split("=")[1].split(":")[0].toLong()
                           ),
                       Point(
                           command[8].split("=")[1].split(",")[0].toLong(),
                           command[9].split("=")[1].toLong()
                       )
                   )
    }


private fun part2(inputList: List<String>): Int {
    return 0
}




