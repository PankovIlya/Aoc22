package org.home

import java.time.Instant.now
import java.util.*


fun solution13() {

    val inputListTest = readInput("inputs/input13test.txt")
    val inputList = readInput("inputs/input13.txt")

    println("Solution 13:")
    println("   test ${part1(inputListTest) == 13 && part2(inputListTest) == 140}")
    println("   part 1 answer ${part1(inputList)}") //5675
    println("   part 2 answer ${part2(inputList)}") //20383
}


private fun part1(inputList: List<String>): Int =
    parseInput(inputList)
        .withIndex()
        .sumOf { (i, item) -> if (compareItems(item.first, item.second) == -1) i + 1 else 0 }


private fun part2(input: List<String>): Int {
    val div1 = parseString("[[2]]")
    val div2 = parseString("[[6]]")

    val res = (parseInput(input) + listOf(Pair(div1, div2)))
        .flatMap { listOf(it.first, it.second) }
        .toMutableSet()
        .sortedWith { o1, o2 -> compareItems(o1, o2) }

    return ((res.indexOf(div1) + 1) * (res.indexOf(div2) + 1))
}

private fun parseInput(inputList: List<String>): List<Pair<List<Item>, List<Item>>> {
    val iter = inputList.iterator()
    val result = mutableListOf<Pair<List<Item>, List<Item>>>()
    while (iter.hasNext()) {
        val str = iter.next()
        if (str.isNotBlank()) {
            result.add(Pair(parseString(str), parseString(iter.next())))
        }
    }
    return result
}


sealed class Item {
    data class Value(val value: Int) : Item()
    data class Array(val value: List<Item>) : Item()
}

fun parseString(line: String): List<Item> {

    fun parseItem(iterator: Iterator<Char>): List<Item> {
        val res: MutableList<Item> = mutableListOf()
        while (iterator.hasNext()) {
            var s = iterator.next()
            if (s.isDigit()) {
                var tmp = ""
                while (s.isDigit()) {
                    tmp += s.toString()
                    s = iterator.next()
                }
                res.add(Item.Value(tmp.toInt()))
            }
            if (s == '[') {
                val tmp = parseItem(iterator)
                res.add(Item.Array(tmp))
            }
            if (s == ']') {
                return res
            }
        }

        return res
    }

    return parseItem(line.toCharArray().iterator())
}


fun compareItems(firstList: List<Item>, secondList: List<Item>): Int {
    val firstIter = firstList.iterator()
    val secondIter = secondList.iterator()
    while (firstIter.hasNext() && secondIter.hasNext()) {
        when (val first = firstIter.next()) {
            is Item.Array -> {
                when (val second = secondIter.next()) {
                    is Item.Array -> {
                        val res = compareItems(first.value, second.value)
                        if (res != 0) return res
                    }
                    is Item.Value -> {
                        val res = compareItems(first.value, listOf(second))
                        if (res != 0) return res
                    }
                }
            }
            is Item.Value -> {
                when (val second = secondIter.next()) {
                    is Item.Array -> {
                        val res = compareItems(listOf(first), second.value)
                        if (res != 0) return res
                    }
                    is Item.Value -> {
                        if (first.value < second.value) {
                            return -1
                        } else if (first.value > second.value) {
                            return 1
                        }
                    }
                }
            }
        }
    }
    return if (firstList.size < secondList.size) -1 else if (firstList.size == secondList.size) 0 else 1
}




