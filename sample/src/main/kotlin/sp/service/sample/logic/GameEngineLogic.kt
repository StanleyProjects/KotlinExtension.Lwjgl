package sp.service.sample.logic

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.input.JoystickMapper
import sp.kx.lwjgl.engine.input.JoystickMapping
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.JoystickButton
import sp.kx.lwjgl.entity.input.KeyboardButton
import sp.kx.math.measure.diff
import sp.kx.math.pointOf
import sp.service.sample.game.entity.StateCommon
import sp.service.sample.game.module.journey.JourneyModule
import sp.service.sample.game.module.mm.MainMenuItem
import sp.service.sample.game.module.mm.MainMenuModule
import sp.service.sample.util.FontInfoUtil
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class GameEngineLogic(private val engine: Engine) : EngineLogic {
    private lateinit var shouldEngineStopUnit: Unit

    override val joystickMapper: JoystickMapper = object : JoystickMapper {
        override fun map(guid: String, buttons: ByteArray, axes: FloatArray): JoystickMapping? {
            return null
        }
    }

    private var state: StateCommon = StateCommon.MAIN_MENU
    private val mm = MainMenuModule(
        engine = engine,
        broadcast = {
            when (it) {
                is MainMenuModule.Broadcast.OnItem -> {
                    when (it.value) {
                        MainMenuItem.NEW_GAME -> {
                            jm.init()
                            state = StateCommon.JOURNEY
                        }
                        MainMenuItem.SETTINGS -> TODO()
                        MainMenuItem.EXIT -> {
                            shouldEngineStopUnit = Unit
                        }
                    }
                }
            }
        }
    )
    private val jm = JourneyModule(
        engine = engine,
        broadcast = {
            when (it) {
                JourneyModule.Broadcast.Exit -> {
                    shouldEngineStopUnit = Unit
                }
            }
        }
    )

    override val inputCallback: EngineInputCallback = object : EngineInputCallback {
        override fun onKeyboardButton(button: KeyboardButton, isPressed: Boolean) {
            when (state) {
                StateCommon.MAIN_MENU -> mm.onKeyboardButton(button, isPressed)
                StateCommon.JOURNEY -> jm.onKeyboardButton(button, isPressed)
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
        val pixelsPerUnit = 16.0 // todo
        val padding = pixelsPerUnit * 1
//        val fps = TimeUnit.SECONDS.toNanos(1).toDouble() / (engine.property.timeNow - engine.property.timeLast)
        val fps = 1.seconds / engine.property.time.diff()
        canvas.drawText(
            info = FontInfoUtil.getFontInfo(height = 16f),
            pointTopLeft = pointOf(x = padding, y = padding),
            color = Color.GREEN,
            text = String.format("%.2f", fps)
        )
        when (state) {
            StateCommon.MAIN_MENU -> mm.onRender(canvas)
            StateCommon.JOURNEY -> jm.onRender(canvas)
        }
    }
}
