package org.home

import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

fun main(args: Array<String>) {
    val inputStream: InputStream = File("inputs/input11.txt").inputStream()

    val inputList = mutableListOf<String>()


    inputStream.bufferedReader().forEachLine {
        inputList.add(it)
    }

    println("answer ${part112(inputList)}")


}

fun part112(inputList: MutableList<String>): Long {
    val monkeyMover = MonkeyMover(buildMonkeys(inputList))
    monkeyMover.move(10000)
    return monkeyMover.getScore()
}


fun part111(inputList: MutableList<String>): Long {
    val monkeyMover = MonkeyMover(buildMonkeys(inputList))
    monkeyMover.move(20)
    return monkeyMover.getScore()
}

class MonkeyMover(
    private val monkeys: List<Monkey>,
    private val BUZY: Long = 3L
) {

    private val score = monkeys.associate { it.id to 0 }.toMutableMap()
    private val productOfDivisors = monkeys
        .map { it.test!! }
        .reduce{ acc, div -> acc * div }

    fun printScore() {
        println(score)
    }

    fun move(n: Int) {
        repeat(n) {
            monkeys.forEach { calc(it) }
            //  println (score)
        }
        //println (score)
    }

    fun getScore(): Long {
        val list = score.values.map { it }.sortedDescending()
        return list[0].toLong() * list[1].toLong()
    }

    private fun calcScore(monkey: Monkey) {
        score[monkey.id] = score[monkey.id]?.plus(monkey.items.size) ?: 0
    }

    private fun calc(monkey: Monkey) {
        calcScore(monkey)
        monkey.items.forEach {
            val value = calcItem(monkey, it)
            val index = getIndex(monkey, value)
            monkeys[index].items.addLast(value)
        }
        monkey.items = LinkedList()
    }

    private fun calcItem(monkey: Monkey, value: Long): Long {
        val (operation, op) = monkey.command!!.split(" ")
        val x = if (op == "old") value else op.toLong()
        return if (operation == "*") {
            (value * x) % productOfDivisors
        } else {
            (value + x) % productOfDivisors
        }
    }

    private fun getIndex(monkey: Monkey, value: Long) =
        if (value % monkey.test!! == 0L) {
            monkey.idxTrue!!
        } else {
            monkey.IdxFalse!!
        }
}

fun buildMonkeys(inputList: MutableList<String>): List<Monkey> {
    var monkey: Monkey? = null
    var monkeys = mutableListOf<Monkey>()
    var idx = -1
    inputList.forEach { op ->
        if (op.startsWith("Monkey")) {
            idx += 1
            monkey = Monkey(idx)
            monkeys.add(monkey!!)
        }
        if (op.startsWith("  Starting items:")) {
            val values = op.split(" ")
            values.subList(4, values.size).map { it.toInt() }.forEach {
                monkey!!.items.add(it.toLong())
            }
        }
        if (op.startsWith("  Operation:")) {
            val values = op.split(" ")
            monkey!!.command = values.subList(values.size - 2, values.size).joinToString(" ")
        }
        if (op.startsWith("  Test: divisible")) {
            val values = op.split(" ")
            monkey!!.test = values.last().toLong()
        }
        if (op.startsWith("    If true:")) {
            val values = op.split(" ")
            monkey!!.idxTrue = values.last().toInt()
        }
        if (op.startsWith("    If false:")) {
            val values = op.split(" ")
            monkey!!.IdxFalse = values.last().toInt()
        }
    }
    return monkeys
}

data class Monkey(
    val id: Int,
    var items: LinkedList<Long> = LinkedList(),
    var command: String? = null,
    var test: Long? = null,
    var idxTrue: Int? = null,
    var IdxFalse: Int? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Monkey

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}

fun part102(inputList: List<String>): Int {
    var x = 1
    val list = getList(inputList)
    var sprite = Sprite()

    val lines = List(6) { mutableListOf<String>() }


    list.forEachIndexed { i, value ->
        val idxLines = i.div(40)
        val idx = i % 40

        if (sprite.position.contains(idx)) {
            lines[idxLines].add("@")
        } else {
            lines[idxLines].add(" ")
        }

        if (!value.startsWith("addx") && !value.startsWith("noop")) {
            x += value.toInt()
            sprite = Sprite(setOf(x - 1, x, x + 1))
        }
    }
    lines.forEach { l -> println(l.joinToString(" ")) }
    return 0
}

data class Sprite(
    val position: Set<Int> = setOf(0, 1, 2)
)

fun part101(inputList: List<String>): Int {
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

    inputList.forEachIndexed { i, command ->
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

//println("answer ${part91(inputList) == 5878}")
//println("answer ${part92(inputList)}")

//println("answer ${part91test()}")

fun part91test(): Int {
    val moveMaker = MoveMaker(Point(3, 3), listOf(Point(2, 4)))
    moveMaker.move("R 1")
    return moveMaker.getTailUniqueCount()
}

fun part92(inputList: java.util.ArrayList<String>): Int {
    val moveMaker = MoveMaker(Point(0, 0), 1.rangeTo(9).map { Point(0, 0) })
    inputList.forEach {
        moveMaker.move(it)
    }
    return moveMaker.getTailUniqueCount()
}


fun part91(inputList: java.util.ArrayList<String>): Int {
    val moveMaker = MoveMaker()
    inputList.forEach {
        moveMaker.move(it)
    }
    return moveMaker.getTailUniqueCount()
}


class MoveMaker(
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
            "R" -> point.x = point.x + 1
            "L" -> point.x = point.x - 1
            "U" -> point.y = point.y + 1
            "D" -> point.y = point.y - 1
            else -> println("Unknown direct = $direct")
        }

    private fun distance(point1: Point, point2: Point): Int =
        ((point1.x - point2.x).toDouble().pow(2) + (point1.y - point2.y).toDouble().pow(2)).pow(0.5).toInt()

    fun getTailUniqueCount() = history.size
}

data class Point(
    var x: Int,
    var y: Int
)


fun part82(inputList: ArrayList<String>): Int {
    val matrix = inputList.map { it.toCharArray().map { c -> c.toString() }.map { s -> s.toInt() } }
    var max = 0

    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, _ ->
            val current = calcView(i, j, matrix)
            if (current > max) {
                max = current
            }
        }
    }
    return max

}

fun calcView(i: Int, j: Int, matrix: List<List<Int>>): Int =
    calcLine(matrix[i], j) * calcLine(matrix.map { it[j] }, i)

fun calcLine(line: List<Int>, index: Int) =
    (index - calcMaxIndexLeft(line.subList(0, index), line[index])) *
            (calcMaxIndexRight(line.subList(index + 1, line.size), line[index]))


fun calcMaxIndexLeft(line: List<Int>, value: Int): Int {
    for (high in line.size - 1 downTo 0) {
        if (line[high] >= value) {
            return high
        }
    }
    return 0
}

fun calcMaxIndexRight(line: List<Int>, value: Int): Int {
    line.forEachIndexed { i, high ->
        if (high >= value) {
            return i + 1
        }
    }
    return line.size
}


fun part81(inputList: ArrayList<String>): Int {
    val matrix = inputList.map { it.toCharArray().map { c -> c.toString() }.map { s -> s.toInt() } }
    var sum = 0

    matrix.forEachIndexed { i, row ->
        row.forEachIndexed { j, value ->
            if (check(i, j, value, matrix)) {
                ++sum
                println(" visible i = $i  j = $j ")
            } else {
                println(" unvisible i = $i  j = $j ")
            }
        }
    }
    return sum
}

fun check(i: Int, j: Int, value: Int, matrix: List<List<Int>>) =
    if (i == 0 || j == 0 || i == matrix.size - 1 || j == matrix[0].size - 1) {
        true
    } else {
        val check1 = checkLine(matrix[i], j, value)
        val check2 = checkLine(matrix.map { it[j] }, i, value)
        check1 || check2
    }

fun checkLine(line: List<Int>, index: Int, value: Int): Boolean {
    val check1 = checkMax(line, 0, index, value)
    val check2 = checkMax(line, index + 1, line.size, value)

    return !check1 || !check2
}

fun checkMax(line: List<Int>, i: Int, j: Int, value: Int): Boolean {
    val list = line.subList(i, j)
    return list.firstOrNull { it >= value }?.let { true } ?: false
}


fun part72(inputList: java.util.ArrayList<String>): Int {
    val main = buildTree(inputList)

    val systemSize = main.sizeOf
    val freeSize = 70000000 - systemSize
    val needSize = 30000000 - freeSize

    return calcNeedCatalog(needSize, main)
}

fun calcNeedCatalog(needSize: Int, main: Node.Catalog): Int {
    var min = Int.MAX_VALUE
    val catalogs = LinkedList<Node.Catalog>()
    catalogs.add(main)

    while (catalogs.size > 0) {
        val catalog = catalogs.pollFirst()

        catalogs.addAll(catalog.files.map { it.value }.filterIsInstance<Node.Catalog>())

        if (catalog.size in needSize until min) {
            min = catalog.size
        }
    }
    return min
}

fun buildTree(inputList: java.util.ArrayList<String>): Node.Catalog {
    val root = Node.Catalog("/", parent = null)
    var current = root

    inputList.forEach { command ->
        if (command.startsWith("\$ cd")) {
            val (_, _, name) = command.split(" ")

            current = when (name) {
                "/" -> root
                ".." -> current.parent ?: current
                else -> current.files[name] as Node.Catalog
            }
        }

        if (!command.startsWith("$")) {
            val (value, name) = command.split(" ")

            if (value == "dir") {
                current.files[name] = Node.Catalog(
                    name = name,
                    parent = current
                )
            } else {
                current.files[name] = Node.File(
                    name = name,
                    size = value.toInt()
                )
            }
        }
    }
    return root
}


sealed class Node(
    open val name: String,
) {
    abstract val sizeOf: Int

    data class File(
        override val name: String,
        var size: Int = 0,
    ) : Node(name) {

        override val sizeOf: Int = size
    }

    data class Catalog(
        override val name: String,
        var size: Int = 0,
        val parent: Catalog?,
        val files: MutableMap<String, Node> = mutableMapOf(),
    ) : Node(name) {

        private var calcSize: Boolean = false

        override val sizeOf: Int by lazy {
            if (calcSize) {
                size
            } else {
                size = files.map { it.value.sizeOf }.sum()
                calcSize = true
                size
            }
        }
    }
}

fun main6(args: Array<String>) {
    val inputStream: InputStream = File("inputs/input6.txt").inputStream()

    println("answer ${foo61(inputStream.bufferedReader().readLine())}")
}

fun foo61(readLine: String): Int {
    val dict = mutableMapOf<Char, Int>()
    val hash = LinkedList<Char>()
    readLine.toCharArray().forEachIndexed { i, c ->
        val value = dict[c]

        if (value == null) {
            dict[c] = 1
        } else {
            dict[c] = value + 1
        }

        if (dict.size == 14) {
            return i + 1
        }
        hash.add(c)
        if (hash.size == 14) {
            val char = hash.pollFirst()
            val value = dict[char]
            if (value != null && value > 1) {
                dict[char] = value - 1
            } else {
                dict.remove(char)
            }

        }
    }
    return 0
}

fun main5(args: Array<String>) {
    val inputStream: InputStream = File("inputs/input5.txt").inputStream()

    val inputList = ArrayList<String>()


    inputStream.bufferedReader().forEachLine {
        inputList.add(it)
    }

    val data = fooParse511(inputList)

    val inputStream2: InputStream = File("inputs/input5.1.txt").inputStream()

    val inputList2 = ArrayList<String>()

    inputStream2.bufferedReader().forEachLine {
        inputList2.add(it)
    }

    println(data)


    val moves = fooParse512(inputList2)

    print("answer ${foo51(data, moves)}")
}

fun foo51(input: List<LinkedList<String>>, moves: List<Move>): String {
    moves.forEach {
        val local = LinkedList<String>()
        for (i in 1.rangeTo(it.i)) {
            local.push(input[it.j - 1].pollLast())
        }
        for (i in 1.rangeTo(it.i)) {
            val x = local.poll()
            input[it.k - 1].addLast(x)
        }
        //println(input)
    }

    return input.map { it.pollLast() }.joinToString(separator = "")

}

data class Move(
    val i: Int,
    val j: Int,
    val k: Int,
)

fun fooParse512(inputList: java.util.ArrayList<String>): List<Move> =
    inputList.map {
        val values = it.split(" ")
        Move(values[1].toInt(), values[3].toInt(), values[5].toInt())
    }


fun fooParse511(inputList: java.util.ArrayList<String>): List<LinkedList<String>> {
    val list = listOf(
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
        LinkedList<String>(),
    )

    inputList.map {
        var values = it.split(" ").map { s -> s.subSequence(1, 2).toString() }.toList()
        values.forEachIndexed { i, x ->
            if (x != "-") {
                list[i].push(x)
            }
        }

    }

    return list
}

fun fooParse41(inputList: java.util.ArrayList<String>): Int =
    inputList.map {
        val values = it.split(",")
        val tasks1 = values[0].split("-")
        val tasks2 = values[1].split("-")
        val task1 = Task(tasks1[0].toInt(), tasks1[1].toInt())
        val task2 = Task(tasks2[0].toInt(), tasks2[1].toInt())

        foo41(task1.i.rangeTo(task1.j).toSet(), task2.i.rangeTo(task2.j).toSet())
    }.sum()

fun foo41(task1: Set<Int>, task2: Set<Int>) =
    if (task1.containsAll(task2) || task2.containsAll(task1)) 1 else {
        val sum = task1.map {
            if (task2.contains(it)) 1 else 0
        }.sum()

        if (sum > 0) 1 else 0
    }

data class Task(
    val i: Int,
    val j: Int
)

fun fooParse32(inputList: java.util.ArrayList<String>): Int {
    val map = charMap()
    var i = 0
    var sum = 0
    while (i < inputList.size) {
        sum += foo32(
            map,
            inputList[i].toSet(),
            inputList[i + 1].toSet(),
            inputList[i + 2].toSet()
        )
        i += 3
    }
    return sum
}

fun foo32(map: MutableMap<Char, Int>, str1: Set<Char>, str2: Set<Char>, str3: Set<Char>) =
    str1.sumOf {
        if (str2.contains(it) && str3.contains(it)) {
            map[it] ?: 0
        } else 0
    }

fun fooParse3(inputList: java.util.ArrayList<String>): Int {
    val map = charMap()
    return inputList.sumOf {
        foo3(
            map,
            it.subSequence(0, it.length / 2).toSet(),
            it.subSequence(it.length / 2, it.length).toSet()
        )
    }
}

fun foo3(map: MutableMap<Char, Int>, str1: Set<Char>, str2: Set<Char>) =
    str1.sumOf {
        if (str2.contains(it)) {
            map[it] ?: 0
        } else 0
    }


fun charMap(): MutableMap<Char, Int> {
    var c: Char
    var i: Int = 1
    val map = mutableMapOf<Char, Int>()
    c = 'a'
    while (c <= 'z') {
        map.put(c, i)
        ++i
        ++c
    }
    c = 'A'
    while (c <= 'Z') {
        map.put(c, i)
        ++i
        ++c
    }
    return map
}


fun fooParse2(inputList: java.util.ArrayList<String>) =
    inputList.map {
        val values = it.split(" ")
        foo2(Card(values[0]), Card(values[1]))
    }.sum()

fun foo2(card1: Card, card2: Card): Int {
    val cartsScore = mapOf(
        Pair(Card("A"), 1),
        Pair(Card("B"), 2),
        Pair(Card("C"), 3),
        Pair(Card("X"), 0),
        Pair(Card("Y"), 3),
        Pair(Card("Z"), 6),
    )

    val combinations = mapOf(
        Pair(CombinationCard(Card("A"), Card("X")), Card("C")),
        Pair(CombinationCard(Card("A"), Card("Y")), Card("A")),
        Pair(CombinationCard(Card("A"), Card("Z")), Card("B")),
        Pair(CombinationCard(Card("B"), Card("Y")), Card("B")),
        Pair(CombinationCard(Card("B"), Card("X")), Card("A")),
        Pair(CombinationCard(Card("B"), Card("Z")), Card("C")),
        Pair(CombinationCard(Card("C"), Card("Z")), Card("A")),
        Pair(CombinationCard(Card("C"), Card("X")), Card("B")),
        Pair(CombinationCard(Card("C"), Card("Y")), Card("C")),
    )


    return (cartsScore[card2] ?: 0) + (cartsScore[combinations[CombinationCard(card1, card2)]] ?: 0)
}


data class CombinationCard(
    val card1: Card,
    val card2: Card,

    )

data class Card(
    val card: String,
)

fun main1() {
    println(trap(arrayOf(4, 2, 0, 3, 2, 5).toIntArray()))
    println(trap(arrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1).toIntArray()))
}

data class Vertex(
    val i: Int,
    val h: Int
)


fun trap(height: IntArray): Int {
    var min = Int.MAX_VALUE
    var sum = 0
    val items = LinkedList<Vertex>()

    for (i in height.indices) {
        val x = height[i]

        if (x < min) {
            min = x
            items.push(Vertex(i, x))
        }

        if (x > min) {
            var curMax = items.first?.h ?: 0
            while (items.size > 0) {
                var prev = items.first
                val h = if (prev.h > x) x else prev.h
                sum += (i - prev.i - 1) * (h - curMax)

                curMax = prev.h
                if (items.first.h < x) {
                    items.pollFirst()
                } else {
                    break
                }

            }
            min = x
            items.push(Vertex(i, x))
        }
    }
    return sum

}