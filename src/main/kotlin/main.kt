import org.openrndr.KEY_ENTER
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.extra.noise.perlinLinear
import org.openrndr.math.Vector2
import org.openrndr.math.map
import kotlin.random.Random

fun generateSeed() = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE)

@ExperimentalUnsignedTypes
fun main() = application {
    var seed = generateSeed()
    var start = .0

    configure {
        width = 900
        height = 900
    }

    program {
        val font = loadFont("src/main/resources/VCR_OSD_MONO_1.001.ttf", 14.0)

        keyboard.keyDown.listen {
            if (it.key == KEY_ENTER) {
                seed = generateSeed()
            }
        }

        extend {
            drawer.clear(ColorRGBa.BLACK)
            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.WHITE
            drawer.strokeWeight = 2.0

            drawer.text(
                "{Seed: ${seed.toUInt().toString(36)}}",
                20.0,
                30.0
            )

            var offset = start

            for (x in 0..width) {
                drawer.lineSegment(
                    Vector2(
                        x.toDouble(),
                        perlinLinear(seed, offset).map(-1.0, 1.0, height * .75, height * .55)
                    ),
                    Vector2(
                        x + .5,
                        perlinLinear(seed, offset).map(-1.0, 1.0, height * .75, height * .55)
                    )
                )
                offset += .1
            }

            start += .2
        }
    }
}
