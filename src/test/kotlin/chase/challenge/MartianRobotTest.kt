package chase.challenge

import chase.challenge.MartianRobot.Companion.parseBoundary
import chase.challenge.MartianRobot.Companion.parseInitialPosition
import chase.challenge.MartianRobot.Companion.parseInstruction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test

/**
 * Test for MartianRobot
 * @author darsana.b
 */
class MartianRobotTest {

    @Test
    fun parseBoundary_pass() {
        assertEquals(Coordinate(5, 4), parseBoundary("5 4"))
        assertEquals(Coordinate(5, 0), parseBoundary("5 0"))
    }

    @Test
    fun parseBoundary_fail() {
        assertThrows(RuntimeException::class.java) {
            parseBoundary("5 4S")
        }
    }

    @Test
    fun parseInitialPosition_pass() {
        assertEquals(Position(Coordinate(5, 4), Orientation.E), parseInitialPosition("5 4 E"))
        assertEquals(Position(Coordinate(5, 0), Orientation.E), parseInitialPosition("5 0 E"))
        assertEquals(Position(Coordinate(5, 0), Orientation.W), parseInitialPosition("5 0 W"))
    }

    @Test
    fun parseInitialPosition_fail() {
        assertThrows(RuntimeException::class.java) {
            parseInitialPosition("5 4 A")
        }
        assertThrows(RuntimeException::class.java) {
            parseInitialPosition("5 4")
        }
    }

    @Test
    fun parseInstruction_pass() {
        assertEquals("FFFFFF".map { e -> Instruction.valueOf(e.toString()) }, parseInstruction("FFFFFFG"))
        assertEquals("RFLFRLR".map { e -> Instruction.valueOf(e.toString()) }, parseInstruction("RFLsFRLR"))
        assertThrows(RuntimeException::class.java) {
            parseInstruction("RFLsFRLR", false)
        }
    }

    @Test
    fun executeInstruction() {
        val robot = MartianRobot()
        val boundary = parseBoundary("5 3")
        var startFrom = parseInitialPosition("1 1 E")
        var instructions = parseInstruction("RFRFRFRF")
        assertEquals("1 1 E", robot.play(boundary, startFrom, instructions))

        startFrom = parseInitialPosition("3 2 N")
        instructions = parseInstruction("FRRFLLFFRRFLL")
        assertEquals("3 3 N LOST", robot.play(boundary, startFrom, instructions))

        startFrom = parseInitialPosition("0 3 W")
        instructions = parseInstruction("LLFFFLFLFL")
        assertEquals("2 3 S", robot.play(boundary, startFrom, instructions))

        startFrom = parseInitialPosition("0 0 W")
        instructions = parseInstruction("LF")
        assertEquals("0 0 S LOST", robot.play(boundary, startFrom, instructions))
    }

}