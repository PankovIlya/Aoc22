package org.home

import java.util.*
import kotlin.math.max


fun solution21() {

    val inputListTest = readInput("inputs/input21test.txt")
    val inputList = readInput("inputs/input21.txt")

    println("Solution 21:")
    println("   test ${part2(inputListTest)}")
    println("   test ${part1(inputListTest) == 152L && part2(inputListTest) == 301L}")
    println("   part 1 answer ${part1(inputList)}") // 2114
    println("   part 2 answer ${part2(inputList)}") //2666
}

private fun part1(inputList: List<String>): Long {
    return parseInput(inputList)["root"]!!.command.value.toLong()
}

private fun part2(inputList: List<String>): Long {
    val monkeys = parseInput(inputList)
    buildReverseMonkey(monkeys)
    return monkeys["humn"]!!.reverseCommand.value.toLong()
}

private fun parseInput(inputList: List<String>): Map<String, Monkey2> {
    val monkeys = inputList.associate { s ->
        val monkey = s.split(": ")
        monkey[0] to Monkey2(monkey[0], monkey[1])
    }

    monkeys.values.forEach { monkey ->
        val command = monkey.commandStr.split(" ")
        monkey.command =
            if (command.size == 1) {
                Command.Number(command[0].toDouble())
            } else {
                val x = monkeys[command[0]]!!
                val y = monkeys[command[2]]!!
                val sign = command[1]
                when (command[1]) {
                    "+" -> Command.Foo(x, y, sign) { x.command.value + y.command.value }
                    "*" -> Command.Foo(x, y, sign) { x.command.value * y.command.value }
                    "/" -> Command.Foo(x, y, sign) { x.command.value / y.command.value }
                    else -> Command.Foo(x, y, sign) { x.command.value - y.command.value }

                }
            }

    }

    return monkeys
}


fun buildReverseMonkey(monkeys: Map<String, Monkey2>) {
    val monkeysList = monkeys.values
    val monkeysCommand = monkeysList.map {
        it to
                when (val command = it.command) {
                    is Command.Foo -> command
                    else -> null
                }
    }.filter { it.second != null }

    val monkeysMap =
        (monkeysCommand.map { it.second!!.monkey1 to it.first } + monkeysCommand.map { it.second!!.monkey2 to it.first }).toMap()

    var monkey = monkeys["humn"]!!
    var lastMonkey = monkey

    while (monkeysMap[monkey] != null) {
        val nextMonkey = monkeysMap[monkey]!!
        val command = (nextMonkey.command as Command.Foo)
        val secondMonkey = if (command.monkey1 == monkey) command.monkey2 else command.monkey1
        monkey.reverseCommand =
            when ((command).sign) {
                "+" -> Command.Foo(nextMonkey, secondMonkey, "-")
                { nextMonkey.reverseCommand.value - secondMonkey.command.value }

                "*" -> Command.Foo(nextMonkey, secondMonkey, "/")
                { nextMonkey.reverseCommand.value / secondMonkey.command.value }

                "-" -> if (command.monkey1 == monkey) {
                    Command.Foo(nextMonkey, secondMonkey, "+")
                    { nextMonkey.reverseCommand.value + secondMonkey.command.value }
                } else {
                    Command.Foo(nextMonkey, secondMonkey, "-")
                    { secondMonkey.command.value - nextMonkey.reverseCommand.value }
                }

                else -> if (command.monkey1 == monkey) {
                    Command.Foo(nextMonkey, secondMonkey, "*")
                    { nextMonkey.reverseCommand.value * secondMonkey.command.value }
                } else {
                    Command.Foo(nextMonkey, secondMonkey, "/")
                    { secondMonkey.command.value / nextMonkey.reverseCommand.value }
                }
            }
        lastMonkey = monkey
        monkey = nextMonkey
    }

    val lastCommand = (lastMonkey.reverseCommand as Command.Foo)
    val root = monkeys["root"]!!
    val second = if (lastCommand.monkey1 == root) lastCommand.monkey2 else lastCommand.monkey1
    root.reverseCommand = Command.Number(second.command.value * 2)
}

sealed class Command {

    data class Number(override val value: Double) : Command()
    data class Foo(
        val monkey1: Monkey2,
        val monkey2: Monkey2,
        val sign: String,
        val foo: () -> Double
    ) : Command() {
        override val value: Double by lazy {
            foo.invoke()
        }
    }

    abstract val value: Double
}

data class Monkey2(
    val name: String,
    val commandStr: String,
) {
    lateinit var command: Command
    lateinit var reverseCommand: Command
}


