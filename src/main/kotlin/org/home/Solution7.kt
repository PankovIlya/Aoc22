package org.home

import java.util.*

fun solution7() {
    val inputList = readInput("inputs/input7.txt")

    println("Solution 7 part 1 answer ${part1(inputList)}")
    println("Solution 7 part 2 answer ${part2(inputList)}")
}

private fun part1(inputList: List<String>): Int {
    val needSize = 10000
    val root = buildTree(inputList)

    root.sizeOf

    return calcSumCatalog(needSize, root)
}

private fun part2(inputList: List<String>): Int {
    val maxSize = 70000000
    val updateSize = 30000000
    val root = buildTree(inputList)

    val systemSize = root.sizeOf
    val freeSize = maxSize - systemSize
    val needSize = updateSize - freeSize

    return calcNeedCatalog(needSize, root)
}

fun calcSumCatalog(needSize: Int, main: Node.Catalog): Int {
    var sum = 0
    val catalogs = LinkedList<Node.Catalog>()
    catalogs.add(main)

    while (catalogs.size > 0) {
        val catalog = catalogs.pollFirst()

        catalogs.addAll(catalog.files.map { it.value }.filterIsInstance<Node.Catalog>())

        if (catalog.size > needSize) {
            sum += catalog.size
        }
    }
    return sum
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

fun buildTree(inputList: List<String>): Node.Catalog {
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
