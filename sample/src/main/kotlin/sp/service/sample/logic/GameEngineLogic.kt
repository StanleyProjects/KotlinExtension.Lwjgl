package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.implementation.entity.geometry.pointOf
import sp.service.sample.util.ResourceUtil
import java.io.InputStream
import java.util.concurrent.TimeUnit

class GameEngineLogic(private val engine: Engine) : EngineLogic {
    companion object {
        private fun getFontInfo(height: Float): FontInfo {
            val name = "JetBrainsMono.ttf"
            return object : FontInfo {
                override val id: String = "${name}_${height}"
                override val height: Float = height

                override fun getInputStream(): InputStream {
                    return ResourceUtil.requireResourceAsStream(name)
                }
            }
        }
    }

    private lateinit var shouldEngineStopUnit: Unit

    override val joystickMapper: JoystickMapper = object : JoystickMapper {
        override fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping? {
            return null
        }
    }

    private val menus = setOf("New game", "Exit")
    private var position = 0

    override val inputCallback: EngineInputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (button) {
                KeyboardButton.ENTER -> {
                    if (isPressed) {
                        if (position == menus.indexOf("Exit")) {
                            shouldEngineStopUnit = Unit
                        }
                    }
                }
                KeyboardButton.W -> {
                    if (isPressed) {
                        if (position == 0) {
                            position = menus.size - 1
                        } else {
                            position--
                        }
                    }
                }
                KeyboardButton.S -> {
                    if (isPressed) {
                        if (position == menus.size - 1) {
                            position = 0
                        } else {
                            position++
                        }
                    }
                }
            }
        }

        override fun onJoystickButton(button: JoystickButton, isPressed: Boolean) {
            // todo
        }
    }

    override fun shouldEngineStop(): Boolean {
        return ::shouldEngineStopUnit.isInitialized
    }

    override fun onRender(canvas: Canvas) {
        val fps = TimeUnit.SECONDS.toNanos(1).toDouble() / (engine.property.timeNow - engine.property.timeLast)
        canvas.drawText(
            info = getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = 0, y = 0),
            color = Color.GREEN,
            text = String.format("%.2f", fps)
        )
        val textHeight = 16f
        val h1 = textHeight * 2.0
        val p = 4.0
        val h = menus.size * h1 + (menus.size - 1) * p
        val y = engine.property.pictureSize.height / 2 - h / 2
        menus.forEachIndexed { index, text ->
            canvas.drawText(
                info = getFontInfo(height = textHeight),
                pointTopLeft = pointOf(x = h1, y = y + index * (h1 + p)),
                color = Color.GREEN,
                text = text
            )
        }
        canvas.drawText(
            info = getFontInfo(height = textHeight),
            pointTopLeft = pointOf(x = h1 / 2, y = y + position * (h1 + p)),
            color = Color.YELLOW,
            text = ">"
        )
    }
}
