enum class Event {
    BATTLE,
    CONSUMABLE,
    POWER_UP,
}

fun manageBattle(player: Player, enemy: Enemy, toaster: Toaster) {
    println("----- BATTLE MANAGEMENT -----")

    // decrement duration of battle effects
    // effects can be stacked so we decrement only one
    // of each 'type'
    player.effects.toSet().filter {
        it.effectType == EffectType.BATTLE
    }.forEach {
        println("Battle effect ${it.name}: ${it.duration - 1}")
        it.duration--
    }

    println("----- BATTLE START -----")
    println(
        "Player ${player.name}: ${player.health} health / ${player.attack} attack / ${player.shield} shield / foresight: ${player.effects.contains(
            Effect.FORESIGHT
        )}"
    )
    println("Enemy ${enemy.name}: ${enemy.health} health / ${enemy.attack} attack")

    while (player.health > 0 && enemy.health > 0) {
        if (player.effects.contains(Effect.FORESIGHT)) {
            // player attack first
            enemy.health -= player.attack
            player.health -= (enemy.attack - player.shield)
        } else {
            // enemy attack first
            player.health -= (enemy.attack - player.shield)
            enemy.health -= player.attack
        }

        if (player.shield > 0) {
            player.shield--
        }
    }

    println("----- BATTLE END -----")
    println("Player ${player.name}: ${player.health} health / ${player.attack} attack / ${player.shield} shield")
    println("Enemy ${enemy.name}: ${enemy.health} health / ${enemy.attack} attack")

    // update player effects
    player.effects.forEach {
        if (it.duration == 0) {
            println("Effect ${it.name} finished for ${player.name}")

            toaster.message = it.endMessage
            toaster.show()
        }
    }
    player.effects.removeIf { it.duration == 0 }

    if (enemy.health <= 0 && player.health > 0) {
        // Win
        println("----- BATTLE WON -----")
        consumeEvent(player, "Enemy defeated! Hurray!", toaster)
    } else {
        // Lose
        println("----- BATTLE LOSE -----")
        consumeEvent(player, "You were defeated! Too bad!", toaster)

        println("----- GAME OVER -----")
    }
}

fun manageConsumable(player: Player, consumable: Consumable, toaster: Toaster) {
    println("----- CONSUMABLE -----")

    if (consumable == Consumable.HEATH_POTION && player.health == player.maxHealth) {
        println("Health potion not needed")
        return
    }

    println("Apply effect ${consumable.effect.name} (${consumable.effect.effectType}) to ${player.name}")
    consumable.effect.apply(player)

    if (consumable.effect.duration > 0) {
        println("Effect ${consumable.effect.name} stored in ${player.name} effects for ${consumable.effect.duration} turn(s)")
        player.effects.add(consumable.effect)
    }

    consumeEvent(player, consumable.label, toaster)
}

fun managePowerUp(player: Player, powerUp: PowerUp, toaster: Toaster) {
    println("----- POWER UP -----")
    println("Apply effect ${powerUp.effect.name} (${powerUp.effect.effectType}) to ${player.name}")
    powerUp.effect.apply(player)
    println("Effect ${powerUp.effect.name} stored in ${player.name} effects for ${powerUp.effect.duration} turn(s)")
    player.effects.add(powerUp.effect)

    consumeEvent(player, powerUp.label, toaster)
}

fun consumeEvent(player: Player, eventLabel: String, toaster: Toaster) {
    // reset event
    player.currentRoom.event = null

    // show toaster
    toaster.message = eventLabel
    toaster.show()
}
