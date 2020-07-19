fun manageBattle(room: Room, player: Player, enemy: Enemy, toaster: Toaster) {
    while (player.health > 0 && enemy.health > 0) {
        // if 'foresight' then player attack first TODO: power up related
        player.health -= enemy.attack
        enemy.health -= player.attack
    }

    if (enemy.health <= 0) {
        room.hasEnemy = false

        toaster.message = "Enemy defeated! Hurray!"
    } else {
        toaster.message = "You were defeated! Too bad!"
    }

    toaster.showToaster()
}
