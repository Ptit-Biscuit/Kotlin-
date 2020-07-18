import org.openrndr.math.Vector2

data class Player(val name: String, var currentRoom: Room, val moves: MutableList<Direction>)

fun updatePlayerPos(player: Player, rooms: List<Room>, direction: Direction) {
    if (player.currentRoom.openings.contains(direction)) {
        val movement = when (direction) {
            Direction.NORTH -> -Vector2.UNIT_Y
            Direction.EAST -> Vector2.UNIT_X
            Direction.SOUTH -> Vector2.UNIT_Y
            Direction.WEST -> -Vector2.UNIT_X
        }

        player.currentRoom = rooms.find { it.pos == player.currentRoom.pos + movement }!!

        val goingBack = player.moves.isNotEmpty() && player.moves.last() == oppositeDirection(direction)
        if (goingBack) {
            player.moves.remove(player.moves.last())
        } else {
            player.moves.add(direction)
        }
    }
}
