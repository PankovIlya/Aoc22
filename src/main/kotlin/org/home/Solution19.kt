package org.home

import java.util.*

fun solution19() {

    val inputListTest = readInput("inputs/input19test.txt")
    val inputList = readInput("inputs/input19.txt")

    println("Solution 19:")
    println("   test ${part1(inputListTest)}")
    //println("   test ${part1(inputListTest) == 33 && part2(inputListTest) == 0}")
    //println("   part 1 answer ${part1(inputList)}") //
    //println("   part 2 answer ${part2(inputList)}") //
}

private fun part1(inputList: List<String>): Int {
    val plans = parseInput(inputList)
    return plans.parallelStream().map { plan ->
        Plant().calc(24, plan.first) * (plan.second)
    }.mapToInt { it }.sum()
}

private fun part2(inputList: List<String>): Int {
    return 0
}

class Plant {

    fun calc(maxStep: Int, plan: Plan): Int {
        val queue = LinkedList<Plan>()
        queue.add(plan)
        var max = 0
        while (queue.size > 0) {
            val currentPlan = queue.pollLast()
            if (currentPlan.step == maxStep) {
                if (max < (currentPlan.resource[ResourceType.GEODE]?: 0)) {
                    max = currentPlan.resource[ResourceType.GEODE]!!
                }
            } else {
                queue.addAll(nextPlans(currentPlan, maxStep)
                    .filter { calcLimit(it, maxStep) >= max }

                )
            }

        }
        return max
    }

    private fun calcLimit(plan: Plan, maxStep: Int): Int {
        return (plan.resource[ResourceType.GEODE] ?: 0) +
                ((plan.resourceRobot[ResourceType.GEODE] ?: 0) * (maxStep - plan.step)) + maxStep - plan.step
    }

    private fun nextPlans(plan: Plan, maxStep : Int): List<Plan> {
        val geodeRobot = plan.robotPlan[ResourceType.GEODE]!!

        return if (checkRobot(geodeRobot, plan) &&
            getRobotStep(geodeRobot, plan) == 1 && ((plan.step + 1) <= maxStep)
        ) {
            listOf(calcPlan(geodeRobot, plan, 1))
        } else {
            val plans = plan.robotPlan.values
                .filter { checkRobot(it, plan) }
                .map{it to  getRobotStep(it, plan)}
                .filter { plan.step + it.second <= maxStep }
                .map { calcPlan(it.first, plan, if (it.second == 0) 1 else it.second) }

            if (plans.isEmpty() && (plan.step + 1) <= maxStep) {
                return listOf(calcPlan(null, plan, 1 ))
            }

            plans.filter { plan.step  <= maxStep }
        }
    }


    private fun calcPlan(robot: Robot?, plan: Plan, robotStep: Int): Plan {
        val newResource = plan.resource.toMutableMap()

        plan.resourceRobot.forEach {
            newResource[it.key] = (newResource[it.key] ?: 0) + (it.value * robotStep)
        }

        val newResourceRobot = plan.resourceRobot.toMutableMap()

        if (robot != null) {
            newResourceRobot[robot.robotType] = (newResourceRobot[robot.robotType] ?: 0) + 1

            robot.cost.forEach {
                newResource[it.resource] = newResource[it.resource]!! - it.count
            }
        }

        return Plan(
            resourceRobot = newResourceRobot,
            resource = newResource,
            robotPlan = plan.robotPlan,
            step = plan.step + robotStep
        )
    }

    private fun getRobotStep(robot: Robot, plan: Plan): Int =
        robot.cost.map {
            if (plan.resource[it.resource]!! > it.count) 1 else 1 + it.count - (plan.resource[it.resource]?:0)
        }.maxOf {it}


    private fun checkRobot(robot: Robot, plan: Plan): Boolean =
        robot.cost.map { (plan.resourceRobot[it.resource] ?: 0) > 0 }.all { it }


}

data class Plan(
    //val robotStep : MutableMap<Robot, List<Int>>,
    val resourceRobot: MutableMap<ResourceType, Int> =
        ResourceType.values().associate { it to if (it == ResourceType.ARE) 1 else 0 }.toMutableMap(),
    val resource: Map<ResourceType, Int> =
        ResourceType.values().associate { it to 0 }.toMutableMap(),
    val robotPlan: Map<ResourceType, Robot>,
    val step: Int = 0
)

data class Robot(
    val robotType: ResourceType,
    val cost: List<Resource>
)

data class Resource(
    val resource: ResourceType,
    val count: Int
)

enum class ResourceType(val resource : String, val priority : Int) {
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
            }.sortedByDescending { it.robotType.priority }.associateBy { it.robotType }

        plans.add(
            Pair(
                Plan(
                    robotPlan = robots
                ), index + 1))
    }
    return plans
}




