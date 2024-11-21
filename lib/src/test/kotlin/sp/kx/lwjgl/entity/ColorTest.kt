package sp.kx.lwjgl.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ColorTest {
    @Test
    fun colorsTest() {
        Color.Black.assert(
            alpha = 0xff,
            red = 0x00,
            green = 0x00,
            blue = 0x00,
        )
        Color.Red.assert(
            alpha = 0xff,
            red = 0xff,
            green = 0x00,
            blue = 0x00,
        )
        Color.Green.assert(
            alpha = 0xff,
            red = 0x00,
            green = 0xff,
            blue = 0x00,
        )
        Color.Blue.assert(
            alpha = 0xff,
            red = 0x00,
            green = 0x00,
            blue = 0xff,
        )
        Color.White.assert(
            alpha = 0xff,
            red = 0xff,
            green = 0xff,
            blue = 0xff,
        )
    }

    @Test
    fun colorOfTest() {
        colorOf(0x01020304).assert(
            alpha = 0x01,
            red = 0x02,
            green = 0x03,
            blue = 0x04,
        )
    }

    @Test
    fun copyTest() {
        val color = colorOf(0x01020304)
        color.copy(alpha = 0x11).assert(
            alpha = 0x11,
            red = 0x02,
            green = 0x03,
            blue = 0x04,
        )
        color.copy(alpha = 0x12).assert(
            alpha = 0x12,
            red = 0x02,
            green = 0x03,
            blue = 0x04,
        )
    }

    companion object {
        private fun Byte.assert(expected: Int, tag: String) {
            assertEquals(expected, toInt().and(0xff), "byte($tag): $this")
        }

        private fun Color.assert(alpha: Int, red: Int, green: Int, blue: Int) {
            this.alpha.assert(expected = alpha, tag = "alpha")
            this.red.assert(expected = red, tag = "red")
            this.green.assert(expected = green, tag = "green")
            this.blue.assert(expected = blue, tag = "blue")
        }
    }
}
