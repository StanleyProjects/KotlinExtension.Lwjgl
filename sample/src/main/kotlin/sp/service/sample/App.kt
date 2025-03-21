package sp.service.sample

import sp.kx.calculations.MutableSize
import sp.kx.lwjgl.engine.Engine
import sp.service.sample.logics.OrthoLogics
import sp.service.sample.logics.PerspectiveLogics
import sp.service.sample.logics.TestLogics

fun main() {
	Engine.run(
//		title = "Test", supplier = ::TestLogics,
//		title = "Ortho", supplier = ::OrthoLogics,
		title = "Perspective", supplier = ::PerspectiveLogics,
		size = MutableSize(640.0, 480.0),
		defaultFontName = "JetBrainsMono.ttf",
	)
}
