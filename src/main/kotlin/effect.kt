import kotlin.math.ceil

enum class EffectType {
    BATTLE,
    PERMANENT,
    ONE_SHOT,
}

enum class Effect(
    val apply: (Player) -> Unit,
    val effectType: EffectType,
    var duration: Int = -1,
    val endMessage: String = ""
) {
    // double damage for 2 battles
    DAMAGE_BOOST(
        {
            it.attack *= 2
            println("Player ${it.name} attack: ${it.attack} (2 battles)")
        },
        EffectType.BATTLE,
        2,
        "Strength back to normal"
    ),

    // increased damage by 20%
    DAMAGE_UP(
        {
            it.attack += ceil(it.attack * .2).toInt()
            println("Player ${it.name} attack: ${it.attack}")
        },
        EffectType.PERMANENT
    ),

    // attack before enemy in battle
    FORESIGHT({}, EffectType.PERMANENT),

    // heal 20% of max health
    HEAL(
        {
            it.health += ceil(it.maxHealth * .2).toInt()

            if (it.health > it.maxHealth) {
                it.health = it.maxHealth
            }

            println("Player ${it.name} health: ${it.health}")
        },
        EffectType.ONE_SHOT
    ),

    // increased max health by 1
    HEALTH_UP(
        {
            it.maxHealth += 1
            it.health += 1

            println("Player ${it.name} max health: ${it.maxHealth}")
        },
        EffectType.PERMANENT
    ),

    // Blocks 3 attacks
    SHIELD(
        {
            it.shield += 3

            println("Player ${it.name} shield: ${it.shield}")
        },
        EffectType.BATTLE,
        3,
        "Shield broke"
    ),
}