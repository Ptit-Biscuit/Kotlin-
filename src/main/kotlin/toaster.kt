import org.openrndr.animatable.Animatable

class Toaster(var message: String) : Animatable() {
    var y: Double = -10.0

    fun showToaster() {
        animate("y", 50.0, 1000)
        delay(2500)
        animate("y", -10.0, 1000)
        complete()
    }
}