import org.openrndr.math.Vector2

enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

fun oppositeDirection(direction: Direction) = Direction.values()[(direction.ordinal + 2) % 4]

fun addDirection(direction: Direction, pos: Vector2) =
    when (direction) {
        Direction.NORTH -> pos - Vector2.UNIT_Y
        Direction.EAST -> pos + Vector2.UNIT_X
        Direction.SOUTH -> pos + Vector2.UNIT_Y
        Direction.WEST -> pos - Vector2.UNIT_X
    }