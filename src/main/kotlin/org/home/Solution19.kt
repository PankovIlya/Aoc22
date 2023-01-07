package org.home

import java.time.Instant
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

import java.util.stream.Collectors
import kotlin.math.max


fun solution19() {

    val inputListTest = readInput("inputs/input19test.txt")
    val inputList = readInput("inputs/input19.txt")

    println("Solution 19:")
    println("   test ${part1(inputListTest) == 33 && part2(inputListTest) == 3472}")
    var time = Instant.now().toEpochMilli()
    println("   part 1 answer ${part1(inputList)} execution time = ${Instant.now().toEpochMilli() - time}ms") // 1115
    time = Instant.now().toEpochMilli()
    println("   part 2 answer ${part2(inputList)} execution time = ${Instant.now().toEpochMilli() - time}ms") // 25056
}

private fun part1(inputList: List<String>): Int =
    parseInput(inputList)
        .parallelStream()
        .map { plan ->
            Plant().calc(24, plan.first) * plan.second
        }.mapToInt { it }.sum()

private fun part2(inputList: List<String>): Int =
    parseInput(inputList)
        .take(3)
        .parallelStream()
        .map { plan ->
            Plant().calc(32, plan.first)
        }.collect(Collectors.toList())
        .fold(1) { a, b -> a * b }


class Plant {

    fun calc(maxStep: Int, plan: Plan): Int {
        val geodeRobot = plan.robotPlan[ResourceType.GEODE]!!
        val queue = LinkedList<Plan>()
        queue.add(plan)
        var max = 0
        while (queue.size > 0) {
            val currentPlan = queue.pollLast()
            if (currentPlan.step == maxStep) {
                max = max(currentPlan.resource[ResourceType.GEODE] ?: 0, max)
            } else {
                val nextPlans = nextPlans(currentPlan, maxStep, geodeRobot)
                    .filter { calcLimit(it, maxStep) >= max }
                queue.addAll(
                    nextPlans
                )
            }

        }
        return max
    }

    private fun nextPlans(plan: Plan, maxStep: Int, geodeRobot: Robot): List<Plan> =
        if (checkRobot(geodeRobot, plan) && getRobotStep(geodeRobot, plan) == 0) {
            listOf(calcPlan(geodeRobot, plan, 1))
        } else {
            val plans = plan.robotPlan.values
                .asSequence()
                .filter {
                    it.robotType == ResourceType.GEODE ||
                            plan.needRobots[it.robotType]!! > plan.resourceRobot[it.robotType]!!
                }
                .filter { checkRobot(it, plan) }
                .map { it to getRobotStep(it, plan) + 1}
                .filter { plan.step + it.second <= maxStep }
                .map { calcPlan(it.first, plan,  it.second) }
                .toList()

            plans.ifEmpty {
                listOf(calcPlan(null, plan, maxStep - plan.step))
            }
        }


    private fun calcPlan(robot: Robot?, plan: Plan, robotStep: Int) =
        Plan(
            resourceRobot = newResourceRobot(robot, plan.resourceRobot.toMutableMap()),
            resource = calcNewResource(robot, plan, plan.resource.toMutableMap(), robotStep),
            robotPlan = plan.robotPlan,
            step = plan.step + robotStep,
            needRobots = plan.needRobots
        )

    private fun newResourceRobot(
        robot: Robot?,
        newResourceRobot: MutableMap<ResourceType, Int>,
    ): MutableMap<ResourceType, Int> {
        if (robot != null) {
            newResourceRobot[robot.robotType] = (newResourceRobot[robot.robotType] ?: 0) + 1
        }
        return newResourceRobot
    }

    private fun calcNewResource(
        robot: Robot?,
        plan: Plan,
        resource: MutableMap<ResourceType, Int>, robotStep: Int
    ): MutableMap<ResourceType, Int> {

        plan.resourceRobot.forEach {
            resource[it.key] = (resource[it.key] ?: 0) + (it.value * robotStep)
        }
        robot?.cost?.forEach {
            resource[it.resource] = resource[it.resource]!! - it.count
        }

        return resource
    }

    private fun getRobotStep(robot: Robot, plan: Plan): Int =
        robot.cost.map {
            if (plan.resource[it.resource]!! >= it.count) 0 else {
                val fact = it.count - (plan.resource[it.resource] ?: 0)
                val planResource = plan.resourceRobot[it.resource]!!
                ceil(fact.toDouble() / planResource).roundToInt()
            }
        }.maxOf { it }


    private fun checkRobot(robot: Robot, plan: Plan): Boolean =
        robot.cost.map { (plan.resourceRobot[it.resource] ?: 0) > 0 }.all { it }

    private fun calcLimit(plan: Plan, maxStep: Int): Int {
        return (plan.resource[ResourceType.GEODE] ?: 0) +
                ((plan.resourceRobot[ResourceType.GEODE] ?: 0) * (maxStep - plan.step)) +
                (maxStep - plan.step) * (maxStep - plan.step - 1) / 2
    }
}

data class Plan(
    val resourceRobot: MutableMap<ResourceType, Int> =
        ResourceType.values().associate { it to if (it == ResourceType.ARE) 1 else 0 }.toMutableMap(),
    val resource: Map<ResourceType, Int> =
        ResourceType.values().associate { it to 0 }.toMutableMap(),
    val robotPlan: Map<ResourceType, Robot>,
    val step: Int = 0,
    val needRobots: Map<ResourceType, Int>
)

data class Robot(
    val robotType: ResourceType,
    val cost: List<Resource>
)

data class Resource(
    val resource: ResourceType,
    val count: Int
)

enum class ResourceType(val resource: String, val priority: Int) {
    ARE("ore", 4),
    CLAY("clay", 3),
    OBSIDIAN("obsidian", 2),
    GEODE("geode", 1);

    companion object {
        fun of(resource: String) =
            values().first { it.resource == resource }
    }
}

private fun parseInput(inputList: List<String>): List<Pair<Plan, Int>> {
    val plans = mutableListOf<Pair<Plan, Int>>()
    inputList.forEachIndexed { index, s ->

        val plan = s.split(" Each ")

        val robots = plan.filter { !it.startsWith("Blueprint") }
            .map {
                val command = it.split(" robot costs ")
                val costs = command[1].split(" and ")
                    .map { cost ->
                        val value = cost.split(" ")
                        Resource(ResourceType.of(value[1]), value[0].toInt())
                    }
                Robot(
                    ResourceType.of(command[0]),
                    costs,
                )
            }.sortedByDescending { it.robotType.priority }

        val needRobots = robots.flatMap { it.cost.map { cost -> cost.resource to cost.count } }
            .groupingBy { it.first }.fold(0) { max, value -> max(max, value.second) }

        plans.add(
            Pair(
                Plan(
                    robotPlan = robots.associateBy { it.robotType },
                    needRobots = needRobots
                ), index + 1
            )
        )
    }
    return plans
}




