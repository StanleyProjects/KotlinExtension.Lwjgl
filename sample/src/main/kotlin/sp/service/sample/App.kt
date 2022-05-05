package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
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

	override fun onRender(canvas: Canvas, property: EngineProperty) {
		canvas.drawPoint(
			color = Color.GREEN,
			point = point(
				x = property.pictureSize.width / 2,
				y = property.pictureSize.height / 2
			)
		)
	}
}

object KeyboardEngineLogic : EngineLogic {
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

	override fun onRender(canvas: Canvas, property: EngineProperty) {
		val fps = TimeUnit.SECONDS.toNanos(1).toDouble() / (property.timeNow - property.timeLast)
		canvas.drawText(
			info = getFontInfo("font.ttf", height = 16f),
			pointTopLeft = point(x = 0, y = 0),
			color = Color.GREEN,
			text = String.format("%.2f", fps)
		)
	}
}

fun main() {
//	val logic: EngineLogic = SampleEngineLogic
	val logic: EngineLogic = KeyboardEngineLogic
	Engine.run(logic)
}
