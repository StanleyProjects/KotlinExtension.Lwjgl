package sp.kx.lwjgl.glfw

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWJoystickCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil
import sp.kx.lwjgl.system.use
import sp.kx.math.MutableSize
import sp.kx.math.Size
import java.nio.ByteBuffer

object GLFWUtil {
    fun getVideoMode(monitorId: Long): GLFWVidMode {
        return GLFW.glfwGetVideoMode(monitorId) ?: error("Failed to get video mode by monitor: $monitorId")
    }

    fun getMonitorSize(monitorId: Long): Size {
        val mode = getVideoMode(monitorId)
        return MutableSize(width = mode.width().toDouble(), height = mode.height().toDouble())
    }

    fun createWindow(
        title: CharSequence,
        size: Size,
        sharePointerId: Long = MemoryUtil.NULL,
    ): Long {
        return createWindow(
            title = title,
            size = size,
            monitorId = MemoryUtil.NULL,
            sharePointerId = sharePointerId,
        )
    }

    fun createWindow(
        title: CharSequence,
        monitorId: Long,
        size: Size = getMonitorSize(monitorId),
        sharePointerId: Long = MemoryUtil.NULL,
    ): Long {
        return GLFW.glfwCreateWindow(
            size.width.toInt(),
            size.height.toInt(),
            title,
            monitorId,
            sharePointerId,
        )
    }

    fun onKeyCallback(
        block: (Long, Int, Int, Int, Int) -> Unit,
    ): GLFWKeyCallback {
        return object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                block(window, key, scancode, action, mods)
            }
        }
    }

    fun onJoystickCallback(
        block: (id: Int, event: Int) -> Unit,
    ): GLFWJoystickCallback {
        return object : GLFWJoystickCallback() {
            override fun invoke(jid: Int, event: Int) {
                block(jid, event)
            }
        }
    }

    fun getWindowSize(windowId: Long, stack: MemoryStack): Size {
        val widthBuffer = stack.mallocInt(1)
        val heightBuffer = stack.mallocInt(1)
        GLFW.glfwGetWindowSize(windowId, widthBuffer, heightBuffer)
        return MutableSize(width = widthBuffer[0].toDouble(), height = heightBuffer[0].toDouble())
    }

    fun getWindowSize(windowId: Long): Size {
        return stackPush().use {
            getWindowSize(windowId, stack = it)
        }
    }

    fun texImage2D(
        textureTarget: Int,
        textureInternalFormat: Int,
        width: Int,
        height: Int,
        texelDataFormat: Int,
        texelDataType: Int,
        pixels: ByteBuffer,
        levelOfDetailNumber: Int = 0,
        textureBorderWidth: Int = 0,
    ) {
        GL11.glTexImage2D(
            textureTarget,
            levelOfDetailNumber,
            textureInternalFormat,
            width,
            height,
            textureBorderWidth,
            texelDataFormat,
            texelDataType,
            pixels,
        )
    }
}

fun Boolean.toGLFWInt(): Int {
    return if (this) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE
}
