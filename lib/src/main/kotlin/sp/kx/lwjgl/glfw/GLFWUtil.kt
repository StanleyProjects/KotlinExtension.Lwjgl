package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.glfw.GLFWWindowCloseCallbackI
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil
import sp.kx.lwjgl.entity.Size
import sp.kx.lwjgl.entity.size
import sp.kx.lwjgl.system.use

object GLFWUtil {
    fun getVideoMode(monitorId: Long): GLFWVidMode {
        return GLFW.glfwGetVideoMode(monitorId) ?: error("Failed to get video mode by monitor: $monitorId")
    }

    fun getMonitorSize(monitorId: Long): Size {
        val mode = getVideoMode(monitorId)
        return size(width = mode.width(), height = mode.height())
    }

    fun createWindow(
        size: Size,
        title: CharSequence,
        monitorPointerId: Long = MemoryUtil.NULL,
        sharePointerId: Long = MemoryUtil.NULL
    ): Long {
        return GLFW.glfwCreateWindow(
            size.width.toInt(),
            size.height.toInt(),
            title,
            monitorPointerId,
            sharePointerId
        )
    }

    fun onKeyCallback(
        block: (Long, Int, Int, Int, Int) -> Unit
    ): GLFWKeyCallback {
        return object: GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                block(window, key, scancode, action, mods)
            }
        }
    }

    fun getWindowSize(windowId: Long, stack: MemoryStack): Size {
        val widthBuffer = stack.mallocInt(1)
        val heightBuffer = stack.mallocInt(1)
        GLFW.glfwGetWindowSize(windowId, widthBuffer, heightBuffer)
        return size(width = widthBuffer[0], height = heightBuffer[0])
    }

    fun getWindowSize(windowId: Long): Size {
        return stackPush().use {
            getWindowSize(windowId, stack = it)
        }
    }
}

fun Boolean.toGLFWInt(): Int {
    return if (this) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE
}
