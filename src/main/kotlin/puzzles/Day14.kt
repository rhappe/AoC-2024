package puzzles

import api.readInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import model.Coordinate
import model.IntCoordinate
import utils.printAnswer
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue

fun main() {
    val input = readInput(day = 14)

    val partOneAnswer = measureTimedValue { Day14.Part01.foo(input) }
    partOneAnswer.printAnswer(label = "Part 2")

    val partTwoAnswer = measureTimedValue { Day14.Part02.foo(input) }
    partTwoAnswer.printAnswer(label = "Part 2")
}

private object Day14 {
    object Part01 {
        fun foo(input: List<String>): Int {
            val initialRobots = parseRobots(input)
            val headquarters = HeadquartersFloor(
                robots = initialRobots,
                floorHeight = 103,
                floorWidth = 101,
            )
            val updatedFloor = headquarters.elapsedTimeBy(100.seconds)
            return updatedFloor.safetyFactor
        }
    }

    object Part02 {
        fun foo(input: List<String>): Int {
//            val initialRobots = parseRobots(input)
//            val initialFloor = HeadquartersFloor(
//                robots = initialRobots,
//                floorHeight = 103,
//                floorWidth = 101,
//            )
//            
//            var iteration = 0 // 47, 82, 150, 183, 253
//            runBlocking {
//                while (true) {
//                    val heightIter = 47 + iteration * 103
//                    val heightIterFloor = initialFloor.elapsedTimeBy(heightIter.seconds)
//                    println("Iteration: $heightIter")
//                    heightIterFloor.print()
//
//                    delay(0.25.seconds)
//
//                    val widthIter = 82 + iteration * 101
//                    val widthIterFloor = initialFloor.elapsedTimeBy(widthIter.seconds)
//                    println("Iteration: $widthIter")
//                    widthIterFloor.print()
//
//                    delay(0.25.seconds)
//
//                    iteration++
//                }
//            }
            return 7051 // I got this answer from looking at the printouts from the commented code lol
        }
    }

    private fun parseRobots(input: List<String>): List<Robot> {
        return input.map {
            val parts = it.split(" ")
            val positionPart = parts[0]
            val velocityPart = parts[1]

            Robot(
                position = parseCoordinate(positionPart.split("=")[1]),
                velocity = parseCoordinate(velocityPart.split("=")[1]),
            )
        }
    }

    private fun parseCoordinate(coordinates: String): IntCoordinate {
        val parts = coordinates.split(",")
        return Coordinate(
            row = parts[1].toInt(),
            col = parts[0].toInt(),
        )
    }

    private data class HeadquartersFloor(
        val robots: List<Robot>,
        val floorHeight: Int,
        val floorWidth: Int,
    ) {
        val safetyFactor: Int by lazy {
            val q1Robots = getRobotsInArea(
                height = 0 until floorHeight / 2,
                width = (floorWidth / 2) + 1..floorWidth,
            )
            val q2Robots = getRobotsInArea(
                height = 0 until floorHeight / 2,
                width = 0 until floorWidth / 2,
            )
            val q3Robots = getRobotsInArea(
                height = (floorHeight / 2) + 1..floorHeight,
                width = 0 until floorWidth / 2,
            )
            val q4Robots = getRobotsInArea(
                height = (floorHeight / 2) + 1..floorHeight,
                width = (floorWidth / 2) + 1..floorWidth,
            )


            q1Robots.size * q2Robots.size * q3Robots.size * q4Robots.size
        }

        private fun getRobotsInArea(height: IntRange, width: IntRange): List<Robot> {
            return robots.filter { it.position.row in height && it.position.col in width }
        }

        fun elapsedTimeBy(duration: Duration): HeadquartersFloor = HeadquartersFloor(
            robots = robots.map {
                correctRobotPosition(robot = it.moved(duration))
            },
            floorHeight = floorHeight,
            floorWidth = floorWidth,
        )

        private fun correctRobotPosition(robot: Robot): Robot {
            val adjustedPosition = robot.position.let {
                Coordinate(
                    row = it.row % floorHeight,
                    col = it.col % floorWidth,
                )
            }
            val correctedPosition = Coordinate(
                row = when {
                    adjustedPosition.row < 0 -> floorHeight + adjustedPosition.row
                    else -> adjustedPosition.row
                },
                col = when {
                    adjustedPosition.col < 0 -> floorWidth + adjustedPosition.col
                    else -> adjustedPosition.col
                }
            )
            return robot.copy(position = correctedPosition)
        }

        fun print() {
            val string = buildString {
                appendLine("_".repeat(floorWidth))
                for (row in 0..floorHeight) {
                    append("|")
                    for (col in 0..floorWidth) {
                        if (robots.any { it.position == Coordinate(row, col) }) {
                            append("X")
                        } else {
                            append(" ")
                        }
                    }
                    append("|")
                    appendLine()
                }
                appendLine("_".repeat(floorWidth))
            }
            print(string)
        }

    }

    data class Robot(
        val position: IntCoordinate,
        val velocity: IntCoordinate,
    ) {
        fun moved(duration: Duration): Robot {
            val times = duration.inWholeSeconds.toInt()
            val totalDistance = Coordinate(
                row = (velocity.row * times),
                col = (velocity.col * times),
            )
            return copy(position = position + totalDistance)
        }
    }
}