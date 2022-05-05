package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineInputCallback
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.input.Key
import sp.kx.lwjgl.entity.input.KeyState
import sp.kx.lwjgl.entity.point

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

	override fun onRender(canvas: Canvas, engineProperty: EngineProperty) {
		canvas.drawPoint(
			color = Color.GREEN,
			point = point(
				x = engineProperty.pictureSize.width / 2,
				y = engineProperty.pictureSize.height / 2
			)
		)
	}
}

object KeyboardEngineLogic : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit
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

	override fun onRender(canvas: Canvas, engineProperty: EngineProperty) {
		// todo
	}
}

fun main() {
//	val logic: EngineLogic = SampleEngineLogic
	val logic: EngineLogic = KeyboardEngineLogic
	Engine.run(logic)
}
