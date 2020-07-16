import org.openrndr.KEY_ENTER
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadFont
import org.openrndr.math.Vector2
import kotlin.math.floor
import kotlin.random.Random

// ---------- SEED ---------- //
fun generateSeed() = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

// ---------- ROOM ---------- //
const val ROOM_WIDTH = 50.0
const val ROOM_OPENING_WIDTH = ROOM_WIDTH / 3.0

val moveDown = Vector2.UNIT_Y * ROOM_WIDTH
val moveRight = Vector2.UNIT_X * ROOM_WIDTH
val moveDownRight = Vector2.ONE * ROOM_WIDTH
val horizontalOpeningStart = Vector2.UNIT_X * ROOM_OPENING_WIDTH
val horizontalOpeningEnd = Vector2(ROOM_WIDTH - ROOM_OPENING_WIDTH, .0)
val verticalOpeningStart = Vector2.UNIT_Y * ROOM_OPENING_WIDTH
val verticalOpeningEnd = Vector2(.0, ROOM_WIDTH - ROOM_OPENING_WIDTH)

data class Room(val pos: Vector2, val openings: MutableCollection<Direction>)

fun generateRooms(rnd: Random, grid: MutableList<MutableList<Boolean>>, num: Int): MutableList<Room> {
    val rooms = mutableListOf<Room>()
    val firstRoomPos = Vector2(floor(grid.size / 2.0), floor(grid[0].size / 2.0 - 1))

    // first room
    grid[firstRoomPos.x.toInt()][firstRoomPos.y.toInt()] = true
    rooms.add(Room(firstRoomPos, mutableSetOf()))

    (1 until num).forEach {
        var room = generateRoom(rnd, rooms[it - 1])

        while (grid[room.pos.x.toInt() % grid.size][room.pos.y.toInt() % grid[0].size]) {
            room = generateRoom(rnd, rooms[it - 1])
        }

        grid[room.pos.x.toInt() % grid.size][room.pos.y.toInt() % grid[0].size] = true
        rooms.add(room)
    }

    return rooms
}

fun generateRoom(rnd: Random, previousRoom: Room): Room {
    // pick a random side
    val side = Direction.values().random(rnd)

    // add opening to previous room
    previousRoom.openings.add(side)

    // calculate new room position
    val pos = when (side) {
        Direction.NORTH -> previousRoom.pos - Vector2.UNIT_Y
        Direction.EAST -> previousRoom.pos + Vector2.UNIT_X
        Direction.SOUTH -> previousRoom.pos + Vector2.UNIT_Y
        Direction.WEST -> previousRoom.pos - Vector2.UNIT_X
    }

    return Room(pos, mutableSetOf(oppositeDirection(side)))
}

// ---------- GRID ---------- //
fun generateGrid(width: Int, height: Int) =
    MutableList(width / ROOM_WIDTH.toInt() - 1) { MutableList(height / ROOM_WIDTH.toInt() - 2) { false } }

// ---------- DRAW UTILS ---------- //
fun toWorldPos(pos: Vector2) =
    Vector2(pos.x * ROOM_WIDTH + ROOM_WIDTH / 2.0, pos.y * ROOM_WIDTH + (ROOM_WIDTH * 1.5))

fun drawRoom(drawer: Drawer, room: Room) {
    // convert grid position to world position
    val worldPos = toWorldPos(room.pos)

    // NORTH side
    if (room.openings.contains(Direction.NORTH)) {
        drawer.lineSegment(worldPos, worldPos + horizontalOpeningStart)
        drawer.lineSegment(worldPos + horizontalOpeningEnd, worldPos + moveRight)
    } else {
        drawer.lineSegment(worldPos, worldPos + moveRight)
    }

    // EAST side
    if (room.openings.contains(Direction.EAST)) {
        drawer.lineSegment(worldPos + moveRight, worldPos + moveRight + verticalOpeningStart)
        drawer.lineSegment(worldPos + moveRight + verticalOpeningEnd, worldPos + moveDownRight)
    } else {
        drawer.lineSegment(worldPos + moveRight, worldPos + moveDownRight)
    }

    // SOUTH side
    if (room.openings.contains(Direction.SOUTH)) {
        drawer.lineSegment(worldPos + moveDown, worldPos + moveDown + horizontalOpeningStart)
        drawer.lineSegment(worldPos + moveDown + horizontalOpeningEnd, worldPos + moveDownRight)
    } else {
        drawer.lineSegment(worldPos + moveDown, worldPos + moveDownRight)
    }

    // WEST side
    if (room.openings.contains(Direction.WEST)) {
        drawer.lineSegment(worldPos, worldPos + verticalOpeningStart)
        drawer.lineSegment(worldPos + verticalOpeningEnd, worldPos + moveDown)
    } else {
        drawer.lineSegment(worldPos, worldPos + moveDown)
    }
}

// ---------- DIRECTION ---------- //
enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

fun oppositeDirection(direction: Direction) = Direction.values()[(direction.ordinal + 2) % 4]

@ExperimentalUnsignedTypes
fun main() = application {
    var seed = generateSeed()
    var rnd = Random(seed)
    val numberOfRooms = 9


    configure {
        width = 900
        height = 600
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)
        var grid = generateGrid(width, height)
        var rooms = generateRooms(rnd, grid, numberOfRooms)

        // listeners
        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = generateSeed()
                rnd = Random(seed)
                grid = generateGrid(width, height)

                rooms.clear()
                rooms = generateRooms(rnd, grid, numberOfRooms)
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            // display useful data
            drawer.text("{Seed: ${seed.toUInt().toString(36)}}", 20.0, 30.0)

            // draw grid points
            grid.forEachIndexed { indexX, x ->
                x.forEachIndexed { indexY, roomPresent ->
                    // green if no room else red
                    drawer.stroke = if (roomPresent) ColorRGBa.RED else ColorRGBa.GREEN

                    drawer.circle(
                        ROOM_WIDTH + indexX.toDouble() * ROOM_WIDTH,
                        (ROOM_WIDTH * 2) + indexY.toDouble() * ROOM_WIDTH,
                        2.0
                    )
                }
            }

            // reset stroke
            drawer.stroke = ColorRGBa.WHITE

            // draw rooms
            rooms.forEach { r ->
                drawRoom(drawer, r)
            }
        }
    }
}
