package sp.kx.lwjgl.util

import org.lwjgl.BufferUtils
import sp.kx.lwjgl.entity.Size
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels

object IOUtil {
    fun createByteBuffer(size: Size): ByteBuffer {
        val capacity = size.width * size.height
        return BufferUtils.createByteBuffer(capacity.toInt())
    }
}

private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
    val newBuffer = BufferUtils.createByteBuffer(newCapacity)
    buffer.flip()
    newBuffer.put(buffer)
    return newBuffer
}

fun InputStream.toByteBuffer(bufferSize: Int): ByteBuffer {
    return Channels.newChannel(this).use { channel ->
        var buffer = BufferUtils.createByteBuffer(bufferSize)
        while (true) {
            val byte = channel.read(buffer)
            if (byte == -1) break
            if (buffer.remaining() == 0) buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2)
        }
        buffer.flip()
        buffer
    }
}
