import org.openrndr.KEY_ENTER
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadFont
import org.openrndr.math.Vector2
import org.openrndr.math.map
import kotlin.random.Random

fun generateSeed() = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

fun generateFirstPos(rnd: Random, minX: Double, maxX: Double, minY: Double, maxY: Double) =
    Vector2(
        rnd.nextDouble().map(.0, 1.0, minX, maxX),
        rnd.nextDouble().map(.0, 1.0, minY, maxY)
    )

enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

data class Room(var pos: Vector2, val width: Int, val openings: Collection<Direction>)

fun generateRoom(drawer: Drawer, room: Room) {
    // NORTH side
    if (room.openings.contains(Direction.NORTH)) {
        drawer.lineSegment(
            room.pos,
            Vector2(
                room.pos.x + room.width / 3.0,
                room.pos.y
            )
        )
        drawer.lineSegment(
            Vector2(
                room.pos.x + room.width - room.width / 3.0,
                room.pos.y
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y
            )
        )
    } else {
        drawer.lineSegment(
            room.pos,
            Vector2(
                room.pos.x + room.width,
                room.pos.y
            )
        )
    }

    // EAST side
    if (room.openings.contains(Direction.EAST)) {
        drawer.lineSegment(
            Vector2(
                room.pos.x + room.width,
                room.pos.y
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width / 3.0
            )
        )
        drawer.lineSegment(
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width - room.width / 3.0
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width
            )
        )
    } else {
        drawer.lineSegment(
            Vector2(
                room.pos.x + room.width,
                room.pos.y
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width
            )
        )
    }

    // SOUTH side
    if (room.openings.contains(Direction.SOUTH)) {
        drawer.lineSegment(
            Vector2(
                room.pos.x,
                room.pos.y + room.width
            ),
            Vector2(
                room.pos.x + room.width / 3.0,
                room.pos.y + room.width
            )
        )
        drawer.lineSegment(
            Vector2(
                room.pos.x + room.width - room.width / 3.0,
                room.pos.y + room.width
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width
            )
        )
    } else {
        drawer.lineSegment(
            Vector2(
                room.pos.x,
                room.pos.y + room.width
            ),
            Vector2(
                room.pos.x + room.width,
                room.pos.y + room.width
            )
        )
    }

    // WEST side
    if (room.openings.contains(Direction.WEST)) {
        drawer.lineSegment(
            room.pos,
            Vector2(
                room.pos.x,
                room.pos.y + room.width / 3.0
            )
        )
        drawer.lineSegment(
            Vector2(
                room.pos.x,
                room.pos.y + room.width - room.width / 3.0
            ),
            Vector2(
                room.pos.x,
                room.pos.y + room.width
            )
        )
    } else {
        drawer.lineSegment(
            room.pos,
            Vector2(
                room.pos.x,
                room.pos.y + room.width
            )
        )
    }
}

@ExperimentalUnsignedTypes
fun main() = application {
    var seed = generateSeed()
    val rnd = Random(seed)
    val roomUnit = 50

    configure {
        width = 900
        height = 900
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)

        var firstRoomPos = generateFirstPos(rnd, 50.0, width - 50.0, 50.0, height - 50.0)
        var firstRoom =
            Room(firstRoomPos, roomUnit, listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST))

        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = generateSeed()
                firstRoomPos = generateFirstPos(rnd, 50.0, width - 50.0, 50.0, height - 50.0)
                firstRoom = Room(
                    firstRoomPos,
                    roomUnit,
                    listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
                )
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            drawer.text(
                "{Seed: ${seed.toUInt().toString(36)}}",
                20.0,
                30.0
            )

            generateRoom(drawer, firstRoom)

            (0..4).forEach { i ->
                generateRoom(
                    drawer,
                    firstRoom.copy().also {
                        it.pos = Vector2(firstRoom.pos.x + (it.width.toDouble() * i), firstRoom.pos.y)
                    }
                )
            }
        }
    }
}
