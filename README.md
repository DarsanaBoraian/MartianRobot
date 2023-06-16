# MartianRobot
Problem: Martian Robots
 
The Problem
------------
The surface of Mars can be modelled by a rectangular grid around which robots are able to
move according to instructions provided from Earth. You are to write a program that
determines each sequence of robot positions and reports the final position of the robot.
A robot position consists of a grid coordinate (a pair of integers: x-coordinate followed by
y-coordinate) and an orientation (N, S, E, W for north, south, east, and west).
A robot instruction is a string of the letters “L”, “R”, and “F” which represent, respectively, the
instructions:
                ● Left : the robot turns left 90 degrees and remains on the current grid point.
                ● Right : the robot turns right 90 degrees and remains on the current grid point.
                ● Forward : the robot moves forward one grid point in the direction of the current
orientation and maintains the same orientation.
 
The direction North corresponds to the direction from grid point (x, y) to grid point (x, y+1).
 
There is also a possibility that additional command types may be required in the future and
provision should be made for this.
Since the grid is rectangular and bounded (…yes Mars is a strange planet), a robot that
moves “off” an edge of the grid is lost forever. However, lost robots leave a robot “scent” that
prohibits future robots from dropping off the world at the same grid point. The scent is left at
the last grid position the robot occupied before disappearing over the edge. An instruction to
move “off” the world from a grid point from which a robot has been previously lost is simply
ignored by the current robot.
 
Approach
--------
Language - Kotlin
Tests - Kotlin Test
 
Enums
                Instruction { L, R, F } // Backward can be added in future.
                Orientation { N, W, S, E }
                Axis { X, Y }
 
Data Model       
                Coordinate(val x: Int, val y: Int, val validate : Boolean = true)
                Position(val coordinate: Coordinate, val orientation: Orientation)
                Scent(val axis: Axis, val position: Int)
               
Exception
                FallenOff(from: Position) : java.lang.RuntimeException("$from LOST")
               
Define a map of functions with the key as current facing direction / orientation. The value funtion should be creating a new Coordinate by moving 1 position based on the direction.
                E -> X + 1             
                N -> Y + 1            
                W -> X - 1            
                S -> Y - 1
               
Define the lower boundary as Coordinate(0, 0). The upper boundary is an input to the play along with the startFrom position and list of instructions.
The co-oridnates on both X and Y are limited to 50 and the program will execute only a max of 100 instructions per command
               
As we run through the instruction starting from the initial position of the Robot, following are the available actions.
                1. Turn the Robo right / left
                2. Forward
                                a. Identify the new Position.
                                b. Check if there is any scent left on x / y axis for the respective position. If so, retain the current position
                                c. Else when there is no scent available and if the boundry is breached (upper or lower limit), add a scent (to Set) for the position and the respective axis, throw Fallen off with the current position.
                                d. Return the new Position
 
The Input
---------
The first line of input is the upper-right coordinates of the rectangular world, the lower-left
coordinates are assumed to be 0, 0.
The remaining input consists of a sequence of robot positions and instructions (two lines per
robot). A position consists of two integers specifying the initial coordinates of the robot and
an orientation (N, S, E, W), all separated by whitespace on one line. A robot instruction is a
string of the letters “L”, “R”, and “F” on one line.
Each robot is processed sequentially, i.e., finishes executing the robot instructions before the
next robot begins execution.
The maximum value for any coordinate is 50.
All instruction strings will be less than 100 characters in length.
 
The Output
----------
For each robot position/instruction in the input, the output should indicate the final grid
position and orientation of the robot. If a robot falls off the edge of the grid the word “LOST”
should be printed after the position and orientation.
 
Sample Input
5 3
1 1 E
RFRFRFRF
3 2 N
FRRFLLFFRRFLL
0 3 W
LLFFFLFLFL
Sample Output
1 1 E
Developer Programming Problem
Red Badger Consulting Limited
Page 2 of 3
3 3 N LOST
2 3 S
 
