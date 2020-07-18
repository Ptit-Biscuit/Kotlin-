import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

val moveDown = Vector2.UNIT_Y * ROOM_WIDTH
val moveRight = Vector2.UNIT_X * ROOM_WIDTH
val moveDownRight = Vector2.ONE * ROOM_WIDTH
val horizontalOpeningStart = Vector2.UNIT_X * ROOM_OPENING_WIDTH
val horizontalOpeningEnd = Vector2(ROOM_WIDTH - ROOM_OPENING_WIDTH, .0)
val verticalOpeningStart = Vector2.UNIT_Y * ROOM_OPENING_WIDTH
val verticalOpeningEnd = Vector2(.0, ROOM_WIDTH - ROOM_OPENING_WIDTH)

fun toWorldPos(pos: Vector2) =
    Vector2(pos.x * ROOM_WIDTH + ROOM_WIDTH / 2.0, pos.y * ROOM_WIDTH + (ROOM_WIDTH * 1.5))

fun drawGridPoints(drawer: Drawer, grid: MutableList<MutableList<Boolean>>) {
    grid.forEachIndexed { indexX, y ->
        y.forEachIndexed { indexY, roomPresent ->
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
}

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

    if (room.hasEnemy) {
        drawEnemy(drawer, Enemy(1, room))
    }
}

fun drawPlayer(drawer: Drawer, player: Player) {
    val crossSection = Vector2(PLAYER_SCALE, PLAYER_SCALE)
    val inverseCrossSection = Vector2(PLAYER_SCALE, -PLAYER_SCALE)

    drawer.stroke = ColorRGBa.GREEN

    // player world pos
    val playerWorldPos = toWorldPos(player.currentRoom.pos) + ROOM_WIDTH / 2.0

    drawer.lineSegment(playerWorldPos + crossSection, playerWorldPos - crossSection)
    drawer.lineSegment(playerWorldPos + inverseCrossSection, playerWorldPos - inverseCrossSection)

    drawer.stroke = ColorRGBa.WHITE
}

fun drawEnemy(drawer: Drawer, enemy: Enemy) {
    drawer.stroke = ColorRGBa.RED
    drawer.fill = ColorRGBa.TRANSPARENT

    // enemy world pos
    val enemyWorldPos = toWorldPos(enemy.room.pos) + ROOM_WIDTH / 2.0

    drawer.circle(enemyWorldPos, ENEMY_SCALE)

    drawer.stroke = ColorRGBa.WHITE
}
