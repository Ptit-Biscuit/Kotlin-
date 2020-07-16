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

val moveDown = Vector2(.0, ROOM_WIDTH)
val moveRight = Vector2(ROOM_WIDTH, .0)
val moveDownRight = Vector2(ROOM_WIDTH, ROOM_WIDTH)
val horizontalOpeningStart = Vector2(ROOM_OPENING_WIDTH, .0)
val horizontalOpeningEnd = Vector2(ROOM_WIDTH - ROOM_OPENING_WIDTH, .0)
val verticalOpeningStart = Vector2(.0, ROOM_OPENING_WIDTH)
val verticalOpeningEnd = Vector2(.0, ROOM_WIDTH - ROOM_OPENING_WIDTH)

data class Room(val pos: Vector2, val openings: MutableCollection<Direction>)

fun generateRooms(rnd: Random, grid: List<MutableList<Boolean>>, num: Int): MutableList<Room> {
    val rooms = mutableListOf<Room>()
    val firstRoomPos = Vector2(floor(grid.size / 2.0), floor(grid[0].size / 2.0))

    // first room
    rooms.add(Room(firstRoomPos, mutableSetOf()))

    (1 until num).forEach {
        rooms.add(generateRoom(rnd, rooms[it - 1]))
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
        Direction.NORTH -> Vector2(previousRoom.pos.x, previousRoom.pos.y - ROOM_WIDTH)
        Direction.EAST -> Vector2(previousRoom.pos.x + ROOM_WIDTH, previousRoom.pos.y)
        Direction.SOUTH -> Vector2(previousRoom.pos.x, previousRoom.pos.y + ROOM_WIDTH)
        Direction.WEST -> Vector2(previousRoom.pos.x - ROOM_WIDTH, previousRoom.pos.y)
    }

    return Room(pos, mutableSetOf(oppositeDirection(side)))
}

// ---------- GRID ---------- //
fun generateGrid(width: Int, height: Int) =
    MutableList(width / ROOM_WIDTH.toInt() - 1) { MutableList(height / ROOM_WIDTH.toInt() - 2) { false } }

fun toWorldPos(pos: Vector2) = Vector2(pos.x * ROOM_WIDTH + ROOM_WIDTH, pos.y * ROOM_WIDTH + (ROOM_WIDTH * 2))

// ---------- DRAW UTILS ---------- //
fun drawRoom(drawer: Drawer, room: Room) {
    // convert grid position to world position
    val worldPos = toWorldPos(room.pos)

    println(room.pos)
    println(worldPos)

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
    val rnd = Random(seed)
    val numberOfRooms = 2

    configure {
        width = 900
        height = 600
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)
        val grid = generateGrid(width, height)
        var rooms = generateRooms(rnd, grid, numberOfRooms)

        // listeners
        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = generateSeed()
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
