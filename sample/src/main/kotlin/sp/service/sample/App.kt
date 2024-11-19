package sp.service.sample

import sp.kx.lwjgl.util.EngineUtil
import sp.kx.math.sizeOf
import sp.service.sample.logic.GameEngineLogic
import sp.service.sample.logic.InputEngineLogic
import sp.service.sample.logic.TestEngineLogic

fun main() {
//	EngineUtil.run(::GameEngineLogic)
	EngineUtil.run(::TestEngineLogic, size = sizeOf(640, 480), title = "foo bar baz")
//	EngineUtil.run(::InputEngineLogic)
}
