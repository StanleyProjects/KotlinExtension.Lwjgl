package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.math.MutableSize
import sp.service.sample.logics.AxisLogics

fun main() {
	Engine.run(
		title = "Axis", supplier = ::AxisLogics,
		size = MutableSize(640.0, 480.0),
		defaultFontName = "JetBrainsMono.ttf",
	)
}
