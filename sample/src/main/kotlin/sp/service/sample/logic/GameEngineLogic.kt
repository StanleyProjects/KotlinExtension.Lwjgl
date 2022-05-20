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
import sp.kx.lwjgl.entity.point
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

    override val inputCallback: EngineInputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            // todo
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
            pointTopLeft = point(x = 0, y = 0),
            color = Color.GREEN,
            text = String.format("%.2f", fps)
        )
    }
}
