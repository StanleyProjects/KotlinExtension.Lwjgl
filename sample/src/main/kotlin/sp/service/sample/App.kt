package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineInputState
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.font.FontInfo
import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState
import sp.kx.lwjgl.entity.point
import sp.service.sample.util.ResourceUtil
import java.io.InputStream
import java.util.concurrent.TimeUnit

/*
private object SampleEngineLogic : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit
	override val inputCallback = object : EngineInputCallback {
		override fun onKey(key: Key, state: KeyState) {
			// ignored
		}
	}

	override fun shouldEngineStop(): Boolean {
		return ::shouldEngineStopUnit.isInitialized
	}

	override fun onRender(
		canvas: Canvas,
		inputState: EngineInputState,
		property: EngineProperty
	) {
		canvas.drawPoint(
			color = Color.GREEN,
			point = point(
				x = property.pictureSize.width / 2,
				y = property.pictureSize.height / 2
			)
		)
	}
}
*/

class KeyboardEngineLogic(private val engine: Engine) : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit

	private fun getFontInfo(name: String, height: Float): FontInfo {
		return object : FontInfo {
			override val id: String = "${name}_${height}"
			override val height: Float = height

			override fun getInputStream(): InputStream {
				return ResourceUtil.requireResourceAsStream(name)
			}
		}
	}

	override val inputCallback = object : EngineInputCallback {
		override fun onKey(key: Key, state: KeyState) {
			when (key) {
				Key.ESCAPE -> {
					when (state) {
						KeyState.RELEASE -> {
							shouldEngineStopUnit = Unit
						}
					}
				}
			}
		}
	}

	override fun shouldEngineStop(): Boolean {
		return ::shouldEngineStopUnit.isInitialized
	}

	override fun onRender(canvas: Canvas) {
		val fps = TimeUnit.SECONDS.toNanos(1).toDouble() / (engine.property.timeNow - engine.property.timeLast)
		canvas.drawText(
			info = getFontInfo("font.ttf", height = 16f),
			pointTopLeft = point(x = 0, y = 0),
			color = Color.GREEN,
			text = String.format("%.2f", fps)
		)
		setOf(
			setOf(Key.Q, Key.W, Key.E, Key.R, Key.T, Key.Y, Key.U, Key.I, Key.O, Key.P),
			setOf(Key.A, Key.S, Key.D, Key.F, Key.G, Key.H, Key.J, Key.K, Key.L),
			setOf(Key.Z, Key.X, Key.C, Key.V, Key.B, Key.N, Key.M)
		).forEachIndexed { y, keys ->
			keys.forEachIndexed { x, key ->
				val state = engine.input.keyboard.getState(key)
				canvas.drawText(
					info = getFontInfo("font.ttf", height = 16f),
					color = if (state == KeyState.PRESS) Color.YELLOW else Color.GREEN,
					pointTopLeft = point(25 + 25 * x, 25 + 25 * y),
					text = key.name
				)
			}
		}
	}
}

fun main() {
//	val logic: EngineLogic = SampleEngineLogic
	Engine.run(::KeyboardEngineLogic)
}
