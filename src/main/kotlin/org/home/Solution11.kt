package org.home


import java.util.*

fun solution11() {

    val inputListTest = readInput("inputs/input11test.txt")
    val inputList = readInput("inputs/input11.txt")
    println("Solution 11:")
    println("   test ${part1(inputListTest) == 10605L && part2(inputListTest) == 2713310158L}")
    println("   part 1 answer ${part1(inputList)}")
    println("   part 2 answer ${part2(inputList)}")
}

private fun part2(inputList: List<String>): Long {
    val monkeys = buildMonkeys(inputList)
    val module = monkeys
        .map { it.test!! }
        .reduce { acc, div -> acc * div }

    val monkeyMover = MonkeyMover(monkeys) { x -> x % module }
    return monkeyMover.move(10000)
}


private fun part1(inputList: List<String>): Long {
    val monkeyMover = MonkeyMover(buildMonkeys(inputList)) { x -> x / 3 }
    return monkeyMover.move(20)
}

private class MonkeyMover(
    private val monkeys: List<Monkey>,
    private val foo: (x: Long) -> Long
) {

    private lateinit var score : MutableMap<Int, Long>

    fun move(n: Int): Long {
        score = monkeys.associate { it.id to 0L }.toMutableMap()

        repeat(n) {
            monkeys.forEach { calc(it) }
        }
        return getScore()
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
            foo((value * x))
        } else {
            foo((value + x))
        }
    }

    private fun getIndex(monkey: Monkey, value: Long) =
        if (value % monkey.test!! == 0L) {
            monkey.idxTrue!!
        } else {
            monkey.IdxFalse!!
        }

    private fun calcScore(monkey: Monkey) {
        score[monkey.id] = score[monkey.id]?.plus(monkey.items.size) ?: 0
    }

    private fun getScore(): Long {
        val list = score.values.map { it }.sortedDescending()
        return list[0] * list[1]
    }
}

private fun buildMonkeys(inputList: List<String>): List<Monkey> {
    var monkey: Monkey? = null
    val monkeys = mutableListOf<Monkey>()
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

private data class Monkey(
    val id: Int,
    var items: LinkedList<Long> = LinkedList(),
    var command: String? = null,
    var test: Long? = null,
    var idxTrue: Int? = null,
    var IdxFalse: Int? = null,
)