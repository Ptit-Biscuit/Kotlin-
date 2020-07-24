import org.openrndr.math.Vector2

class Player(
    val name: String,
    var maxHealth: Int = 20,
    var health: Int = 20,
    var attack: Int = 1,
    var shield: Int = 0,
    var currentRoom: Room,
    val moves: MutableList<Direction> = mutableListOf(),
    val effects: MutableList<Effect> = mutableListOf()
) {
    fun updatePos(rooms: List<Room>, direction: Direction) {
        if (this.currentRoom.openings.contains(direction)) {
            println("Player ${this.name} goes $direction")

            val movement = when (direction) {
                Direction.NORTH -> -Vector2.UNIT_Y
                Direction.EAST -> Vector2.UNIT_X
                Direction.SOUTH -> Vector2.UNIT_Y
                Direction.WEST -> -Vector2.UNIT_X
            }

            this.currentRoom = rooms.find { it.pos == this.currentRoom.pos + movement }!!

            val goingBack = this.moves.isNotEmpty() && this.moves.last() == oppositeDirection(direction)
            if (goingBack) {
                this.moves.remove(this.moves.last())
            } else {
                this.moves.add(direction)
            }
        }
    }

    fun reset(room: Room) {
        this.maxHealth = 10
        this.health = 10
        this.attack = 1
        this.shield = 0
        this.currentRoom = room
        this.moves.clear()
        this.effects.clear()
    }
}
