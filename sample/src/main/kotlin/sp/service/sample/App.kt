package sp.service.sample

import sp.kx.lwjgl.util.EngineUtil
import sp.service.sample.logic.GameEngineLogic

fun main() {
	EngineUtil.run(::GameEngineLogic)
}
