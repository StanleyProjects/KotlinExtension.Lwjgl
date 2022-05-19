package sp.kx.lwjgl.entity

interface Color {
    companion object {
        const val MAX_VALUE = 1f
        const val MIN_VALUE = 0f

        val WHITE = color(MAX_VALUE, MAX_VALUE, MAX_VALUE)
        val BLACK = color(MIN_VALUE, MIN_VALUE, MIN_VALUE)
        val GREEN = color(MIN_VALUE, MAX_VALUE, MIN_VALUE)
        val BLUE = color(MIN_VALUE, MIN_VALUE, MAX_VALUE)
        val YELLOW = color(MAX_VALUE, MAX_VALUE, MIN_VALUE)
    }

    val red: Float
    val green: Float
    val blue: Float
    val alpha: Float
}

private data class ColorImpl(
    override val red: Float,
    override val green: Float,
    override val blue: Float,
    override val alpha: Float
): Color

fun color(
    red: Float,
    green: Float,
    blue: Float,
    alpha: Float = Color.MAX_VALUE
): Color {
    val expectedRange = Color.MIN_VALUE..Color.MAX_VALUE
    mapOf(
        "red" to red,
        "green" to green,
        "blue" to blue
    ).forEach { (key, value) ->
        check(value in expectedRange) {
            "The color $key is out of range $expectedRange!"
        }
    }
    check(alpha in expectedRange) {
        "The alpha value is out of range $expectedRange!"
    }
    return ColorImpl(
        red = red,
        green = green,
        blue = blue,
        alpha = alpha
    )
}
