import org.openrndr.*
import org.openrndr.animatable.Animatable
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.math.Vector2
import kotlin.random.Random

// ---------- SEED ---------- //
fun generateSeed() = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

// ---------- GRID ---------- //
fun generateGrid(width: Int, height: Int) =
    MutableList(width / ROOM_WIDTH.toInt() - 1) { MutableList(height / ROOM_WIDTH.toInt() - 2) { false } }

// ---------- ROOM ---------- //
const val ROOM_WIDTH = 50.0
const val ROOM_OPENING_WIDTH = ROOM_WIDTH / 3.0

// ---------- PLAYER ---------- //
const val PLAYER_SCALE = ROOM_WIDTH / 5.0

// ---------- ENEMIES ---------- //
const val ENEMY_SPAWN_THRESHOLD = .75
const val ENEMY_SCALE = ROOM_WIDTH / 3.0

@ExperimentalUnsignedTypes
fun main() = application {
    // seed
    var seed = generateSeed()
    var rnd = Random(seed)
    // rooms
    var numberOfRooms = 9
    var roomsToDraw = 0
    // player
    val player = Player("toto", 10, 1, Room(Vector2.ZERO, mutableListOf(), false), mutableListOf())

    // user interface
    var debugView = false

    configure {
        width = 900
        height = 600
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)
        var grid = generateGrid(width, height)
        var rooms = generateRooms(rnd, grid, numberOfRooms)

        val toaster = Toaster("")

        // update players position
        player.currentRoom = rooms.first()

        // listeners
        keyboard.keyDown.listen {
            // debug view
            if (it.key == KEY_TAB) {
                debugView = !debugView
            }

            // reset all
            if (it.name == "r") {
                seed = generateSeed()
                rnd = Random(seed)

                numberOfRooms = 9
                roomsToDraw = 0

                grid = generateGrid(width, height)

                rooms.clear()
                rooms = generateRooms(rnd, grid, numberOfRooms)

                player.health = 10
                player.currentRoom = rooms.first()
                player.moves.clear()
            }

            // add one room => TODO: power up related
            if (it.name == "k") {
                val room = generateRoom(rnd, grid, rooms.last())

                if (room != null) {
                    numberOfRooms++
                    rooms.add(room)
                } else {
                    println("DEAD END!")
                }
            }

            // draw next room => TODO: power up related
            if (it.name == "l") {
                if (roomsToDraw < numberOfRooms - 1) {
                    roomsToDraw++
                }
            }

            // clear last room => TODO: power up related
            if (it.name == "j") {
                if (roomsToDraw > 0) {
                    roomsToDraw--
                }
            }

            // player movements
            if (it.key == KEY_ARROW_UP) {
                updatePlayerPos(player, rooms, Direction.NORTH)
            }

            if (it.key == KEY_ARROW_RIGHT) {
                updatePlayerPos(player, rooms, Direction.EAST)
            }

            if (it.key == KEY_ARROW_DOWN) {
                updatePlayerPos(player, rooms, Direction.SOUTH)
            }

            if (it.key == KEY_ARROW_LEFT) {
                updatePlayerPos(player, rooms, Direction.WEST)
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            // animation
            toaster.updateAnimation()

            if (toaster.hasAnimations()) {
                drawer.text(toaster.message, 350.0, toaster.y)
            }

            // display player data
            drawer.text(
                "${player.name} has ${player.health} heart${if (player.health > 1) "s" else ""} left",
                20.0,
                30.0
            )
            drawer.text("sword: ${player.attack} damage", 20.0, 50.0)

            if (debugView) {
                // display seed
                drawer.text("{Seed: ${seed.toUInt().toString(36)}}", width - 150.0, 30.0)

                // draw grid points
                drawGridPoints(drawer, grid)
            }

            // draw rooms
            (0..roomsToDraw).forEach { drawRoom(drawer, rooms[it]) }

            // draw player
            drawPlayer(drawer, player)

            // manage battle
            if (player.currentRoom.hasEnemy && player.health > 0) {
                manageBattle(player.currentRoom, player, Enemy(2, 1), toaster)
            }
        }
    }
}
