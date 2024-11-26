package sp.service.sample

import sp.kx.lwjgl.engine.Engine
import sp.kx.math.sizeOf
import sp.service.sample.logics.TestEngineLogics

fun main() {
//	EngineUtil.run(::GameEngineLogic)
	Engine.run(::TestEngineLogics, size = sizeOf(640, 480), title = "foo bar baz")
//	EngineUtil.run(::PolygonsEngineLogics, size = sizeOf(640, 480), title = "Polygons")
//	EngineUtil.run(::InputEngineLogic)
}
