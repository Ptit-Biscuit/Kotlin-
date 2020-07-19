enum class Consumable(val label: String, val effect: Effect) {
    HEATH_POTION("Health restored", Effect.HEAL),
    SHIELD_POTION("You block some damage", Effect.SHIELD),
    STRENGTH_POTION("Strength increased temporarily", Effect.DAMAGE_BOOST),
}
