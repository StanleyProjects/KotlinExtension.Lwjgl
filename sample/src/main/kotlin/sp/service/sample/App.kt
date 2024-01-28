package sp.service.sample

import sp.kx.lwjgl.util.EngineUtil
import sp.service.sample.logic.GameEngineLogic
import sp.service.sample.logic.InputEngineLogic
import sp.service.sample.logic.TestEngineLogic

fun main() {
//	EngineUtil.run(::GameEngineLogic)
//	EngineUtil.run(::TestEngineLogic)
	EngineUtil.run(::InputEngineLogic)
}
