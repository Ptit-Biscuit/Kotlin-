import org.openrndr.*
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

// ---------- POWER UPS ---------- //
const val POWER_UP_SPAWN_THRESHOLD = .95
const val POWER_UP_SCALE = ROOM_WIDTH / 3.0

// ---------- CONSUMABLES ---------- //
const val CONSUMABLE_SPAWN_THRESHOLD = .90
const val CONSUMABLE_SCALE = ROOM_WIDTH / 3.2

@ExperimentalUnsignedTypes
fun main() = application {
    println("----- NEW GAME -----")

    // seed
    var seed = generateSeed()
    println("Seed: ${seed.toUInt().toString(36)}")

    var rnd = Random(seed)
    // rooms
    var numberOfRooms = 9
    // player
    val player = Player("toto", currentRoom = Room(Vector2.ZERO, mutableListOf(), null))
    println("Player: ${player.name}")

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
        println(rooms.toString())

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
                println("----- NEW GAME -----")
                seed = generateSeed()
                println("Seed: ${seed.toUInt().toString(36)}")
                rnd = Random(seed)

                numberOfRooms = 9

                grid = generateGrid(width, height)

                rooms.clear()
                rooms = generateRooms(rnd, grid, numberOfRooms)
                println(rooms.toString())

                player.reset(rooms.first())
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

            // player movements
            if (it.key == KEY_ARROW_UP) {
                player.updatePos(rooms, Direction.NORTH)
            }

            if (it.key == KEY_ARROW_RIGHT) {
                player.updatePos(rooms, Direction.EAST)
            }

            if (it.key == KEY_ARROW_DOWN) {
                player.updatePos(rooms, Direction.SOUTH)
            }

            if (it.key == KEY_ARROW_LEFT) {
                player.updatePos(rooms, Direction.WEST)
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

            // draw player data
            drawPlayerData(drawer, player)

            if (debugView) {
                drawDebugView(drawer, width, seed, grid, player)
            }

            // draw rooms
            rooms.forEach {
                drawRoom(drawer, it)
            }

            // draw player
            drawPlayer(drawer, player)

            // manage events
            when (player.currentRoom.event) {
                Event.BATTLE -> {
                    if (player.health > 0) {
                        manageBattle(player, Enemy("Noob", 2, 1), toaster)
                    }

                    // update player effects
                    player.effects
                        .filter { it.effectType == EffectType.BATTLE }
                        .forEach {
                            it.duration--

                            if (it.duration == 0) {
                                println("Effect ${it.name} finished for ${player.name}")
                                it.endEffect(player)
                                toaster.message = it.endMessage
                                toaster.show()
                            }
                        }
                    player.effects.removeIf { it.duration == 0 }
                }
                Event.CONSUMABLE -> manageConsumable(player, Consumable.values().random(rnd), toaster)
                Event.POWER_UP -> managePowerUp(player, PowerUp.values().random(rnd), toaster)
                null -> {
                    // do nothing
                }
            }
        }
    }
}
