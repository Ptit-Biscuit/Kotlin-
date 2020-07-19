import org.openrndr.animatable.Animatable

class Toaster(var message: String) : Animatable() {
    var y: Double = -10.0

    fun show() {
        animate("y", 50.0, 250)
        delay(1000)
        animate("y", -10.0, 250)
        complete()
    }
}