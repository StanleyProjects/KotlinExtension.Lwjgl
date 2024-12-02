package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.math.sizeOf
import sp.service.sample.logics.InputEngineLogics
import sp.service.sample.logics.PolygonsEngineLogics
import sp.service.sample.logics.TestEngineLogics

fun main() {
//	EngineUtil.run(::GameEngineLogic)
//	Engine.run(::TestEngineLogics, size = sizeOf(640, 480), title = "foo bar baz")
//	Engine.run(::PolygonsEngineLogics, size = sizeOf(640 * 2, 480 * 2), title = "Polygons")
	Engine.run(::InputEngineLogics, size = sizeOf(640, 480), title = "Input")
}
