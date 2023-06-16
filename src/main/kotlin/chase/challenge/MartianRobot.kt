package chase.challenge

//Enums
enum class Instruction { L, R, F }
enum class Orientation { N, W, S, E }
enum class Axis { X, Y }

//Models
data class Coordinate(val x: Int, val y: Int, val validate : Boolean = true) {
    init {
        if (validate && (x > 50 || y > 50)) throw RuntimeException("Exceeded Boundary Limit")
    }
    override fun toString(): String = "$x $y"
}
data class Position(val coordinate: Coordinate, val orientation: Orientation) {
    override fun toString(): String = "$coordinate ${orientation.name}"
}
data class Scent(val axis: Axis, val position: Int)

//Exception
class FallenOff(from: Position) : java.lang.RuntimeException("$from LOST")

/**
 * Martian Robot
 * @author darsana.b
 *
 * @Desc
 * The surface of Mars can be modelled by a rectangular grid around which robots are able to
 * move according to instructions provided from Earth. You are to write a program that
 * determines each sequence of robot positions and reports the final position of the robot.
 * A robot position consists of a grid coordinate (a pair of integers: x-coordinate followed by
 * y-coordinate) and an orientation (N, S, E, W for north, south, east, and west).
 * A robot instruction is a string of the letters “L”, “R”, and “F” which represent, respectively, the
 * instructions:
 * ● Left : the robot turns left 90 degrees and remains on the current grid point.
 * ● Right : the robot turns right 90 degrees and remains on the current grid point.
 * ● Forward : the robot moves forward one grid point in the direction of the current
 * orientation and maintains the same orientation.
 * The direction North corresponds to the direction from grid point (x, y) to grid point (x, y+1)
 *
 * There is also a possibility that additional command types may be required in the future and
 * provision should be made for this.
 * Since the grid is rectangular and bounded (…yes Mars is a strange planet), a robot that
 * moves “off” an edge of the grid is lost forever. However, lost robots leave a robot “scent” that
 * prohibits future robots from dropping off the world at the same grid point. The scent is left at
 * the last grid position the robot occupied before disappearing over the edge. An instruction to
 * move “off” the world from a grid point from which a robot has been previously lost is simply
 * ignored by the current robot.
 *
 * The first line of input is the upper-right coordinates of the rectangular world, the lower-left
 * coordinates are assumed to be 0, 0.
 * The remaining input consists of a sequence of robot positions and instructions (two lines per
 * robot). A position consists of two integers specifying the initial coordinates of the robot and
 * an orientation (N, S, E, W), all separated by whitespace on one line. A robot instruction is a
 * string of the letters “L”, “R”, and “F” on one line.
 * Each robot is processed sequentially, i.e., finishes executing the robot instructions before the
 * next robot begins execution.
 * The maximum value for any coordinate is 50.
 * All instruction strings will be less than 100 characters in length
 *
 */
class MartianRobot {
    private val scentsLeft = mutableSetOf<Scent>()

    /**
     * Play the Robot with instructions coming from command line
     */
    fun playFromCommandLine() {
        lateinit var startFrom: Position
        val boundary = parseBoundary(readln())
        do {
            try {
                startFrom = parseInitialPosition(readln())
                val instructions = parseInstruction(readln())
                println(play(boundary, startFrom, instructions))
            } catch (e: Exception) {
                println(e.message)
            }
        } while (true)
    }

    /**
     * Move the robot as per the instructions provided with the set boundary limit
     * @param boundary Coordinate
     * @param startFrom Position
     * @param instructions List<Instruction>
     * @return String the Final Position of the Robot
     */
    fun play(boundary: Coordinate, startFrom: Position, instructions: List<Instruction>): String {
        return try {
            val standingAt = executeInstructions(boundary, startFrom, instructions.take(100))
            standingAt.toString()
        } catch (ex: FallenOff) {
            ex.message!!
        }
    }

    /**
     * Executes the instructions to move from the start position
     */
    private fun executeInstructions(
        boundary: Coordinate,
        startFrom: Position,
        instructions: List<Instruction>
    ): Position {
        // Recurse for every instruction by moving from the current position accordingly
        fun moveRecursive(index: Int, lastPos: Position): Position {
            return if (index < instructions.size) {
                val newPos = when (instructions[index]) {
                    Instruction.R -> right(lastPos)
                    Instruction.L -> left(lastPos)
                    Instruction.F -> forward(boundary, lastPos)
                }
                moveRecursive(index + 1, newPos)
            } else lastPos
        }
        return moveRecursive(0, startFrom)
    }

    private fun right(position: Position): Position =
        when (position.orientation) {
            Orientation.E -> position.copy(orientation = Orientation.S)
            Orientation.S -> position.copy(orientation = Orientation.W)
            Orientation.W -> position.copy(orientation = Orientation.N)
            Orientation.N -> position.copy(orientation = Orientation.E)
        }

    private fun left(position: Position): Position =
        when (position.orientation) {
            Orientation.E -> position.copy(orientation = Orientation.N)
            Orientation.N -> position.copy(orientation = Orientation.W)
            Orientation.W -> position.copy(orientation = Orientation.S)
            Orientation.S -> position.copy(orientation = Orientation.E)
        }

    private fun forward(boundary: Coordinate, position: Position): Position {
        val coordinate = functionMap[position.orientation]!!(position.coordinate)
        return if (scentsLeft.contains(Scent(Axis.X, coordinate.x)) || scentsLeft.contains(
                Scent(
                    Axis.Y,
                    coordinate.y
                )
            )
        ) {
            // No movement required. Learn from the LOST Robot's scent
            position
        } else if (coordinate.x > boundary.x || coordinate.x < baseBoundary.x) {
            //Leave a scent on X Axis before falling off
            scentsLeft.add(Scent(Axis.X, coordinate.x))
            throw FallenOff(position)
        } else if (coordinate.y > boundary.y || coordinate.y < baseBoundary.y) {
            //Leave a scent on Y Axis before falling off
            scentsLeft.add(Scent(Axis.Y, coordinate.y))
            throw FallenOff(position)
        } else position.copy(coordinate = coordinate)
    }

    companion object {
        val baseBoundary = Coordinate(0, 0)

        val functionMap = mapOf<Orientation, (Coordinate) -> Coordinate>(
            Pair(Orientation.E) { coordinate -> coordinate.copy(x = coordinate.x + 1, validate = false) },
            Pair(Orientation.N) { coordinate -> coordinate.copy(y = coordinate.y + 1, validate = false) },
            Pair(Orientation.S) { coordinate -> coordinate.copy(y = coordinate.y - 1, validate = false) },
            Pair(Orientation.W) { coordinate -> coordinate.copy(x = coordinate.x - 1, validate = false) }
        )

        fun parseBoundary(input: String?): Coordinate {
            input?.let {
                val splits = it.split(" ", limit = 2)
                try {
                    return Coordinate(splits[0].toInt(), splits[1].toInt())
                } catch (e: Exception) {
                    throw RuntimeException("Unable to parse the input Co-orindates from $input: ${e.message}")
                }
            } ?: throw RuntimeException("Invalid coordinates: $input")
        }

        fun parseInitialPosition(input: String?): Position {
            input?.let {
                val splits = it.split(" ", limit = 3)
                try {
                    return Position(Coordinate(splits[0].toInt(), splits[1].toInt()), Orientation.valueOf(splits[2]))
                } catch (e: Exception) {
                    throw RuntimeException("Unable to parse the initial position from $input: ${e.message}")
                }
            } ?: throw RuntimeException("Invalid input: $input")
        }

        fun parseInstruction(input: String?, ignoreInvalid: Boolean = true): List<Instruction> {
            input?.let {
                return it.flatMap { entry ->
                    try {
                        listOf(Instruction.valueOf(entry.toString()))
                    } catch (e: Exception) {
                        if (!ignoreInvalid) throw java.lang.RuntimeException("Invalid instruction found: $entry")
                        else {
                            println("Ignored: $entry")
                            listOf()
                        }
                    }
                }
            } ?: throw RuntimeException("Invalid input: $input")
        }
    }
}

fun main() {
    MartianRobot().playFromCommandLine()
}