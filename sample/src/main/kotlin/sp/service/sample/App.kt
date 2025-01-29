package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.math.sizeOf
import sp.service.sample.logics.CellsEngineLogics
import sp.service.sample.logics.InputEngineLogics
import sp.service.sample.logics.PolygonsEngineLogics
import sp.service.sample.logics.TestEngineLogics
import sp.service.sample.logics.TextsEngineLogics

fun main() {
	Engine.run(
//		title = "Input", supplier = ::InputEngineLogics,
//		title = "Test", supplier = ::TestEngineLogics,
//		title = "Texts", supplier = ::TextsEngineLogics,
//		title = "Cells", supplier = ::CellsEngineLogics,
		title = "Polygons", supplier = ::PolygonsEngineLogics,
//		size = sizeOf(640, 480),
		defaultFontName = "JetBrainsMono.ttf",
//		defaultFontName = "OpenSans.ttf",
	)
}
