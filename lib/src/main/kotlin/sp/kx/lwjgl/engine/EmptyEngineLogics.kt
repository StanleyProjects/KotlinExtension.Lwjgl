package sp.kx.lwjgl.engine

import sp.kx.lwjgl.entity.Canvas

internal object EmptyEngineLogics : EngineLogics {
    override val inputCallback: EngineInputCallback
        get() = error("${this::class.java.name}:inputCallback is not supported!")

    override fun shouldEngineStop(): Boolean {
        error("${this::class.java.name}:shouldEngineStop is not supported!")
    }

    override fun onRender(canvas: Canvas) {
        error("${this::class.java.name}:onRender($canvas) is not supported!")
    }

    override fun onPreLoop() {
        error("${this::class.java.name}:onPreLoop is not supported!")
    }
}
