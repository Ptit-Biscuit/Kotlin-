import org.openrndr.KEY_ENTER
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.noise.perlinLinear
import org.openrndr.math.Vector2
import org.openrndr.math.map

fun getRandomSeed(length: Int): String = List(length) { (('A'..'Z') + ('0'..'9')).random() }.joinToString("")

fun getSeedValue(value: String): Long {
    return value.asSequence().map {
        when (it) {
            in ('A'..'Z') -> ('A'..'Z').indexOf(it)
            else -> it.toInt()
        }
    }.joinToString("").toLongOrNull() ?: getSeedValue(value.dropLast(2))
}

fun main() = application {
    val length = 10
    var seed = getRandomSeed(length)
    var seedValue = getSeedValue(seed)

    var start = .0

    configure {
        width = 900
        height = 900
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)

        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = getRandomSeed(length)
                seedValue = getSeedValue(seed)
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            drawer.text(
                "{Seed: $seed, Value: $seedValue}",
                20.0,
                30.0
            )

            var offset = start

            for (x in 0..width) {
                drawer.lineStrip(
                    listOf(
                        Vector2(
                            x + .0,
                            perlinLinear(seedValue.toInt(), offset).map(-1.0, 1.0, height * .75, height * .55)
                        ),
                        Vector2(
                            x + .1,
                            perlinLinear(seedValue.toInt(), offset).map(-1.0, 1.0, height * .75, height * .55)
                        )
                    )
                )
                offset += .1
            }

            start += .2
        }
    }
}
