import org.openrndr.KEY_ENTER
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadFont
import org.openrndr.math.Vector2
import kotlin.random.Random

// ---------- SEED ---------- //
fun generateSeed() = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

// ---------- ROOM ---------- //
const val ROOM_WIDTH = 50
const val ROOM_OPENING_WIDTH = ROOM_WIDTH / 3.0

val moveDown = Vector2(.0, .0 + ROOM_WIDTH)
val moveRight = Vector2(.0 + ROOM_WIDTH, .0)
val moveDownRight = Vector2(.0 + ROOM_WIDTH, .0 + ROOM_WIDTH)
val horizontalOpeningStart = Vector2(.0 + ROOM_OPENING_WIDTH, .0)
val horizontalOpeningEnd = Vector2(.0 + ROOM_WIDTH - ROOM_OPENING_WIDTH, .0)
val verticalOpeningStart = Vector2(.0, .0 + ROOM_OPENING_WIDTH)
val verticalOpeningEnd = Vector2(.0, .0 + ROOM_WIDTH - ROOM_OPENING_WIDTH)

data class Room(val pos: Vector2, val openings: MutableCollection<Direction>)

fun generateRooms(rnd: Random, width: Int, height: Int): MutableList<Room> {
    val rooms = mutableListOf<Room>()
    val firstRoomPos = Vector2((width / 2.0) - (ROOM_WIDTH / 2), (height / 2.0) - (ROOM_WIDTH / 2))

    // first room
    rooms.add(Room(firstRoomPos, mutableSetOf()))

    (1..5).forEach {
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

// ---------- DRAW UTILS ---------- //
fun drawRoom(drawer: Drawer, room: Room) {
    // NORTH side
    if (room.openings.contains(Direction.NORTH)) {
        drawer.lineSegment(room.pos, room.pos + horizontalOpeningStart)
        drawer.lineSegment(room.pos + horizontalOpeningEnd, room.pos + moveRight)
    } else {
        drawer.lineSegment(room.pos, room.pos + moveRight)
    }

    // EAST side
    if (room.openings.contains(Direction.EAST)) {
        drawer.lineSegment(room.pos + moveRight, room.pos + moveRight + verticalOpeningStart)
        drawer.lineSegment(room.pos + moveRight + verticalOpeningEnd, room.pos + moveDownRight)
    } else {
        drawer.lineSegment(room.pos + moveRight, room.pos + moveDownRight)
    }

    // SOUTH side
    if (room.openings.contains(Direction.SOUTH)) {
        drawer.lineSegment(room.pos + moveDown, room.pos + moveDown + horizontalOpeningStart)
        drawer.lineSegment(room.pos + moveDown + horizontalOpeningEnd, room.pos + moveDownRight)
    } else {
        drawer.lineSegment(room.pos + moveDown, room.pos + moveDownRight)
    }

    // WEST side
    if (room.openings.contains(Direction.WEST)) {
        drawer.lineSegment(room.pos, room.pos + verticalOpeningStart)
        drawer.lineSegment(room.pos + verticalOpeningEnd, room.pos + moveDown)
    } else {
        drawer.lineSegment(room.pos, room.pos + moveDown)
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

    configure {
        width = 900
        height = 600
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)
        var rooms = generateRooms(rnd, width, height)

        // listeners
        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = generateSeed()
                rooms.clear()
                rooms = generateRooms(rnd, width, height)
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            // display useful data
            drawer.text("{Seed: ${seed.toUInt().toString(36)}}", 20.0, 30.0)

            // draw rooms
            rooms.forEach { r ->
                drawRoom(drawer, r)
            }
        }
    }
}
