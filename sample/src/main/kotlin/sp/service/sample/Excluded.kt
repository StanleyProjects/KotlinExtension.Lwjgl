package sp.service.sample

import org.lwjgl.opengl.GL11
import sp.kx.math.Point
import sp.kx.math.angleOf
import sp.kx.math.distanceOf
import sp.kx.math.moved
import sp.kx.math.pointOf
import java.nio.DoubleBuffer

@Deprecated("sp.kx.math.distanceOf") // todo hypot
internal fun distanceOf(point: Point): Double {
//    return distanceOf(Point.Center, point)
//    return distanceOf(
//        aX = 0.0,
//        aY = 0.0,
//        bX = point.x,
//        bY = point.y,
//    )
    return kotlin.math.hypot(x = point.x, y = point.y)
}

@Deprecated("sp.kx.math.angleOf")
internal fun angleOf(point: Point): Double {
//    return angleOf(Point.Center, point)
//    return angleOf(
//        aX = 0.0,
//        aY = 0.0,
//        bX = point.x,
//        bY = point.y,
//    )
    return kotlin.math.atan2(y = point.y, x = point.x)
}

@Deprecated("sp.kx.math.Matrix")
internal interface Matrix {
    val m00: Double; val m01: Double; val m02: Double; val m03: Double
    val m10: Double; val m11: Double; val m12: Double; val m13: Double
    val m20: Double; val m21: Double; val m22: Double; val m23: Double
    val m30: Double; val m31: Double; val m32: Double; val m33: Double
}

@Deprecated("sp.kx.math.mut")
internal fun Matrix.mut(): MutableMatrix {
    return MutableMatrix(
        m00 = m00, m01 = m01, m02 = m02, m03 = m03,
        m10 = m10, m11 = m11, m12 = m12, m13 = m13,
        m20 = m20, m21 = m21, m22 = m22, m23 = m23,
        m30 = m30, m31 = m31, m32 = m32, m33 = m33,
    )
}

@Deprecated("sp.kx.math.MutableMatrix")
internal class MutableMatrix(
    override var m00: Double, override var m01: Double, override var m02: Double, override var m03: Double,
    override var m10: Double, override var m11: Double, override var m12: Double, override var m13: Double,
    override var m20: Double, override var m21: Double, override var m22: Double, override var m23: Double,
    override var m30: Double, override var m31: Double, override var m32: Double, override var m33: Double,
): Matrix {
    constructor() : this(
        m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = 0.0,
        m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = 0.0,
        m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = 0.0,
        m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
    )

    constructor(other: Matrix) : this(
        m00 = other.m00, m01 = other.m01, m02 = other.m02, m03 = other.m03,
        m10 = other.m10, m11 = other.m11, m12 = other.m12, m13 = other.m13,
        m20 = other.m20, m21 = other.m21, m22 = other.m22, m23 = other.m23,
        m30 = other.m30, m31 = other.m31, m32 = other.m32, m33 = other.m33,
    )
}

@Deprecated("sp.kx.math.identity")
internal fun MutableMatrix.identity() {
    m00 = 1.0; m01 = 0.0; m02 = 0.0; m03 = 0.0
    m10 = 0.0; m11 = 1.0; m12 = 0.0; m13 = 0.0
    m20 = 0.0; m21 = 0.0; m22 = 1.0; m23 = 0.0
    m30 = 0.0; m31 = 0.0; m32 = 0.0; m33 = 1.0
}

@Deprecated("sp.kx.math.set")
internal fun MutableMatrix.set(other: Matrix) {
    m00 = other.m00; m01 = other.m01; m02 = other.m02; m03 = other.m03
    m10 = other.m10; m11 = other.m11; m12 = other.m12; m13 = other.m13
    m20 = other.m20; m21 = other.m21; m22 = other.m22; m23 = other.m23
    m30 = other.m30; m31 = other.m31; m32 = other.m32; m33 = other.m33
}

//@Deprecated("sp.kx.math.ortho")
//internal fun MutableMatrix.ortho(width: Double, height: Double) {
//    m00 = 2.0 / width
//    m11 = -2.0 / height
//    m22 = -2.0
//    m30 = -1.0
//    m31 = 1.0
//    m32 = -1.0
//}

@Deprecated("sp.kx.math.ortho")
internal fun MutableMatrix.ortho(l: Double, t: Double, r: Double, b: Double, zNear: Double, zFar: Double) {
    m00 = 2.0 / (r - l)
    m11 = 2.0 / (t - b)
    m22 = 2.0 / (zNear - zFar)
    m30 = (r + l) / (l - r)
    m31 = (t + b) / (b - t)
    m32 = (zFar + zNear) / (zNear - zFar)
}

@Deprecated("sp.kx.math.ortho")
internal fun MutableMatrix.ortho(l: Double, t: Double, r: Double, b: Double) {
    val rm00 = 2.0 / (r - l)
    val rm11 = 2.0 / (t - b)
    val rm22 = 2.0
    val rm30 = (l + r) / (l - r)
    val rm31 = (t + b) / (b - t)
    val rm32 = 1.0
    m30 = m00 * rm30 + m10 * rm31 + m20 * rm32 + m30
    m31 = m01 * rm30 + m11 * rm31 + m21 * rm32 + m31
    m32 = m02 * rm30 + m12 * rm31 + m22 * rm32 + m32
    m33 = m03 * rm30 + m13 * rm31 + m23 * rm32 + m33
    m00 = m00 * rm00
    m01 = m01 * rm00
    m02 = m02 * rm00
    m03 = m03 * rm00
    m10 = m10 * rm11
    m11 = m11 * rm11
    m12 = m12 * rm11
    m13 = m13 * rm11
    m20 = m20 * rm22
    m21 = m21 * rm22
    m22 = m22 * rm22
    m23 = m23 * rm22
}

@Deprecated("sp.kx.math.ortho")
internal fun MutableMatrix.ortho(r: Double, b: Double) {
    val rm00 = 2.0 / r
    val rm11 = -2.0 / b
    val rm22 = 2.0
    m30 += m10 - m00 + m20
    m31 += m11 - m01 + m21
    m32 += m12 - m02 + m22
    m33 += m13 - m03 + m23
    m00 *= rm00
    m01 *= rm00
    m02 *= rm00
    m03 *= rm00
    m10 *= rm11
    m11 *= rm11
    m12 *= rm11
    m13 *= rm11
    m20 *= rm22
    m21 *= rm22
    m22 *= rm22
    m23 *= rm22
}

@Deprecated("sp.kx.math.rotateX")
internal fun MutableMatrix.rotateX(value: Double) {
    m11 *= java.lang.Math.cos(value)
    m12 *= -java.lang.Math.sin(value)
    m21 *= java.lang.Math.sin(value)
    m22 *= java.lang.Math.cos(value)
}

@Deprecated("sp.kx.math.rotateY")
internal fun MutableMatrix.rotateY(value: Double) {
    m00 *= java.lang.Math.cos(value)
    m02 *= -java.lang.Math.sin(value)
    m20 *= java.lang.Math.sin(value)
    m22 *= java.lang.Math.cos(value)
}

@Deprecated("sp.kx.math.translate")
internal fun MutableMatrix.translate(dX: Double, dY: Double, dZ: Double) {
    m30 = Math.fma(m00, dX, Math.fma(m10, dY, Math.fma(m20, dZ, m30)))
    m31 = Math.fma(m01, dX, Math.fma(m11, dY, Math.fma(m21, dZ, m31)))
    m32 = Math.fma(m02, dX, Math.fma(m12, dY, Math.fma(m22, dZ, m32)))
}

private fun load(buffer: DoubleBuffer, matrix: Matrix) {
    buffer.put(0,  matrix.m00)
        .put(1,  matrix.m01)
        .put(2,  matrix.m02)
        .put(3,  matrix.m03)
        .put(4,  matrix.m10)
        .put(5,  matrix.m11)
        .put(6,  matrix.m12)
        .put(7,  matrix.m13)
        .put(8,  matrix.m20)
        .put(9,  matrix.m21)
        .put(10, matrix.m22)
        .put(11, matrix.m23)
        .put(12, matrix.m30)
        .put(13, matrix.m31)
        .put(14, matrix.m32)
        .put(15, matrix.m33)
}

internal fun onMatrix(matrix: Matrix, buffer: DoubleBuffer, block: () -> Unit) {
    GL11.glPushMatrix()
    load(buffer = buffer, matrix = matrix)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glLoadMatrixd(buffer)
    block()
    GL11.glPopMatrix()
}

@Deprecated("sp.kx.math.Point")
internal interface Point3D {
    val x: Double
    val y: Double
    val z: Double
}

@Deprecated("sp.kx.math.mut")
internal fun Point3D.mut(): MutablePoint3D {
    return MutablePoint3D(
        x = x,
        y = y,
        z = z,
    )
}

@Deprecated("sp.kx.math.copy")
internal fun Point3D.copy(x: Double = this.x, y: Double = this.y, z: Double = this.z): Point3D {
    return MutablePoint3D(
        x = x,
        y = y,
        z = z,
    )
}

@Deprecated("sp.kx.math.MutablePoint")
internal class MutablePoint3D(
    override var x: Double,
    override var y: Double,
    override var z: Double,
) : Point3D {
    fun translate(x: Double, y: Double, z: Double) {
        this.x += x
        this.y += y
        this.z += z
    }

    fun translate(point: Point3D) {
        x += point.x
        y += point.y
        z += point.z
    }

    fun rotateXOld(point: Point3D, radians: Double) {
        x -= point.x
        y -= point.y
        z -= point.z
        // [ 1 0 0 ] [ x ] = [ 1 * x + 0 * x + 0 * x ] = [ x ]
        // [ 0 c-s ] [ y ] = [ 0 * y + c * y - s * y ] = [ c * y - s * y ]
        // [ 0 s c ] [ z ] = [ 0 * z + s * z + c * z ] = [ s * z + c * z ]
        val c = kotlin.math.cos(radians)
        val s = kotlin.math.sin(radians)
        y = c * y - s * y
        z = s * z + c * z
        //
        x += point.x
        y += point.y
        z += point.z
    }

    fun rotateX(point: Point3D, radians: Double) {
        val distance = distanceOf(aX = point.z, aY = point.y, bX = z, bY = y)
        val angle = angleOf(aX = point.z, aY = point.y, bX = z, bY = y)
        z = point.z + distance * kotlin.math.cos(angle + radians)
        y = point.y + distance * kotlin.math.sin(angle + radians)
    }

    fun rotateYOld(point: Point3D, radians: Double) {
        x -= point.x
        y -= point.y
        z -= point.z
        // [ c 0 s ] [ x ] = [ c * x + 0 * x + s * x ]   [ c * x + s * x ]
        // [ 0 1 0 ] [ y ] = [ 0 * y + 1 * y + 0 * y ] = [ y ]
        // [-s 0 c ] [ z ] = [-s * z + 0 * z + c * z ]   [-s * z + c * z ]
        val c = kotlin.math.cos(radians)
        val s = kotlin.math.sin(radians)
        x = c * x + s * x
        z = c * z - s * z
        //
        x += point.x
        y += point.y
        z += point.z
    }

    fun rotateY(point: Point3D, radians: Double) {
        val distance = distanceOf(aX = point.x, aY = point.z, bX = x, bY = z)
        val angle = angleOf(aX = point.x, aY = point.z, bX = x, bY = z)
        x = point.x + distance * kotlin.math.cos(angle + radians)
        z = point.z + distance * kotlin.math.sin(angle + radians)
    }

    fun rotateZ(point: Point3D, radians: Double) {
        val distance = distanceOf(aX = point.x, aY = point.y, bX = x, bY = y)
        val angle = angleOf(aX = point.x, aY = point.y, bX = x, bY = y)
        x = point.x + distance * kotlin.math.cos(angle + radians)
        y = point.y + distance * kotlin.math.sin(angle + radians)
    }
}
