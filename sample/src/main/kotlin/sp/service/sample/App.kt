package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.math.sizeOf
import sp.service.sample.logics.AntiAliasingEngineLogics
import sp.service.sample.logics.BlendingEngineLogics
import sp.service.sample.logics.CellsEngineLogics
import sp.service.sample.logics.InputEngineLogics
import sp.service.sample.logics.PolygonsEngineLogics
import sp.service.sample.logics.TestEngineLogics
import sp.service.sample.logics.TextsEngineLogics
import sp.service.sample.logics.TranslateLogics
import sp.service.sample.logics.VectorsEngineLogics
import org.lwjgl.glfw.GLFW
import sp.service.sample.logics.AffineLogics

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
		title = "Affine", supplier = ::AffineLogics,
		size = sizeOf(640, 480),
//		refreshRate = 144.0,
//		monitorIdSupplier = GLFW::glfwGetPrimaryMonitor,
//		monitorIdSupplier = { GLFW.glfwGetMonitors()?.get(1) ?: error("No monitor!") },
		defaultFontName = "JetBrainsMono.ttf",
//		defaultFontName = "OpenSans.ttf",
	)
}
