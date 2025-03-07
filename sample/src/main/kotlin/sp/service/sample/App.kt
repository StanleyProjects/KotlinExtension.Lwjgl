package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.service.sample.logics.AntiAliasingEngineLogics
import sp.service.sample.logics.BlendingEngineLogics
import sp.service.sample.logics.CellsEngineLogics
import sp.service.sample.logics.InputEngineLogics
import sp.service.sample.logics.PolygonsEngineLogics
import sp.service.sample.logics.TestEngineLogics
import sp.service.sample.logics.TextsEngineLogics
import sp.service.sample.logics.TranslateLogics
import sp.service.sample.logics.VectorsEngineLogics
import sp.kx.math.MutableSize
import sp.service.sample.logics.AffineLogics
import sp.service.sample.logics.AxisLogics
import sp.service.sample.logics.CubeLogics
import sp.service.sample.logics.MatrixLogics

fun main() {
	Engine.run(
//		title = "Input", supplier = ::InputEngineLogics,
//		title = "Test", supplier = ::TestEngineLogics,
//		title = "Texts", supplier = ::TextsEngineLogics,
//		title = "Cells", supplier = ::CellsEngineLogics,
//		title = "Polygons", supplier = ::PolygonsEngineLogics,
//		title = "Vectors", supplier = ::VectorsEngineLogics,
//		title = "AntiAliasing", supplier = ::AntiAliasingEngineLogics,
//		title = "Blending", supplier = ::BlendingEngineLogics,
//		title = "Translate", supplier = ::TranslateLogics,
//		title = "Affine", supplier = ::AffineLogics,
//		title = "Matrix", supplier = ::MatrixLogics,
//		title = "Cube", supplier = ::CubeLogics,
		title = "Axis", supplier = ::AxisLogics,
		size = MutableSize(640.0, 480.0),
		defaultFontName = "JetBrainsMono.ttf",
//		defaultFontName = "OpenSans.ttf",
	)
}
