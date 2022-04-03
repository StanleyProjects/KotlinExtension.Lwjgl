package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.lwjgl.engine.EngineLogic
import sp.kx.lwjgl.engine.EngineProperty
import sp.kx.lwjgl.entity.Canvas
import sp.kx.lwjgl.entity.Color
import sp.kx.lwjgl.entity.point

private object SampleEngineLogic : EngineLogic {
	private lateinit var shouldEngineStopUnit: Unit

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

fun main() {
	println("Hello Lwjgl!")
	val logic: EngineLogic = SampleEngineLogic
	Engine.run(logic)
}
