import org.openrndr.math.Vector2
import kotlin.math.floor
import kotlin.random.Random

data class Room(val pos: Vector2, val openings: MutableCollection<Direction>, var hasEnemy: Boolean)

fun generateRooms(rnd: Random, grid: MutableList<MutableList<Boolean>>, num: Int): MutableList<Room> {
    val rooms = mutableListOf<Room>()

    // first room and update grid
    val firstRoomPos = Vector2(floor(grid.size / 2.0), floor(grid[0].size / 2.0 - 1))
    val firstRoom = Room(firstRoomPos, mutableSetOf(), false)
    grid[firstRoomPos.x.toInt()][firstRoomPos.y.toInt()] = true
    rooms.add(firstRoom)

    (1 until num).forEach {
        val room = generateRoom(rnd, grid, rooms[it - 1])

        if (room != null) {
            rooms.add(room)
        } else {
            println("DEAD END!")
        }
    }

    return rooms
}

fun generateRoom(rnd: Random, grid: MutableList<MutableList<Boolean>>, previousRoom: Room): Room? {
    // pick a random side
    var side = Direction.values().random(rnd)
    val sideTried = mutableListOf(side)

    // calculate new room position
    var pos = addDirection(side, previousRoom.pos)

    while (
        (pos.x < .0) || (pos.x >= grid.size) ||
        (pos.y < .0) || (pos.y >= grid[0].size) ||
        grid[pos.x.toInt()][pos.y.toInt()]
    ) {
        val remainingSides = Direction.values().subtract(sideTried)

        // dead end
        if (remainingSides.isEmpty())
            return null

        // pick a new random side
        side = remainingSides.random(rnd)
        sideTried.add(side)

        // recalculate pos
        pos = addDirection(side, previousRoom.pos)
    }

    // add opening to previous room
    previousRoom.openings.add(side)

    // update grid
    grid[pos.x.toInt()][pos.y.toInt()] = true
    return Room(pos, mutableSetOf(oppositeDirection(side)), rnd.nextDouble(.1, 1.0) > ENEMY_SPAWN_THRESHOLD)
}
