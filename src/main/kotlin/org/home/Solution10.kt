package org.home

fun solution10() {

    val inputListTest = readInput("inputs/input10test.txt")
    val inputList = readInput("inputs/input10.txt")

    println("Solution 10:")
    println("   test ${part1(inputListTest) == 13140}")
    println("   part 1 answer ${part1(inputList)}")

    val answer = part2(inputList)

    println("   part 2 answer ")
    answer.forEach { l -> println(l.joinToString(" ")) }
}


private fun part2(inputList: List<String>): List<MutableList<String>> {
    var x = 1
    val list = getList(inputList)
    var sprite = setOf(0, 1, 2)
    val size = 40

    val lines = List(6) { mutableListOf<String>() }


    list.forEachIndexed { i, value ->
        val idxLines = i.div(size)
        val idx = i % size

        if (sprite.contains(idx)) {
            lines[idxLines].add("#")
        } else {
            lines[idxLines].add(" ")
        }

        if (!value.startsWith("addx") && !value.startsWith("noop")) {
            x += value.toInt()
            sprite = setOf(x - 1, x, x + 1)
        }
    }
    return lines
}

private fun part1(inputList: List<String>): Int {
    val index = listOf(20, 60, 100, 140, 180, 220)
    var x = 1
    var sum = 0
    val list = getList(inputList)

    list.forEachIndexed { i, value ->
        if (index.contains(i + 1)) {
            sum += (i + 1) * x
        }
        if (!value.startsWith("addx") && !value.startsWith("noop")) {
            x += value.toInt()
        }
    }
    return sum
}

fun getList(inputList: List<String>): List<String> {
    val list = mutableListOf<String>()

    inputList.forEach {command ->
        if (command.startsWith("addx")) {
            val (_, value) = command.split(" ")
            list.add("addx")
            list.add(value)
        } else {
            list.add("noop")
        }
    }

    return list
}