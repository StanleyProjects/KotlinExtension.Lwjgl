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
        m00 = 0.0, m01 = 0.0, m02 = 0.0, m03 = 0.0,
        m10 = 0.0, m11 = 0.0, m12 = 0.0, m13 = 0.0,
        m20 = 0.0, m21 = 0.0, m22 = 0.0, m23 = 0.0,
        m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 0.0,
    )

    constructor(other: Matrix) : this(
        m00 = other.m00, m01 = other.m01, m02 = other.m02, m03 = other.m03,
        m10 = other.m10, m11 = other.m11, m12 = other.m12, m13 = other.m13,
        m20 = other.m20, m21 = other.m21, m22 = other.m22, m23 = other.m23,
        m30 = other.m30, m31 = other.m31, m32 = other.m32, m33 = other.m33,
    )

    companion object {
        fun ofRotationX(radians: Double): Matrix {
            val c = kotlin.math.cos(radians)
            val s = kotlin.math.sin(radians)
            return MutableMatrix(
                m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = 0.0,
                m10 = 0.0, m11 = c, m12 = -s, m13 = 0.0,
                m20 = 0.0, m21 = s, m22 = c, m23 = 0.0,
                m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
            )
        }

        fun ofRotationY(radians: Double): Matrix {
            val c = kotlin.math.cos(radians)
            val s = kotlin.math.sin(radians)
            return MutableMatrix(
                m00 = c, m01 = 0.0, m02 = s, m03 = 0.0,
                m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = 0.0,
                m20 = -s, m21 = 0.0, m22 = c, m23 = 0.0,
                m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
            )
        }

        fun ofRotationZ(radians: Double): Matrix {
            val c = kotlin.math.cos(radians)
            val s = kotlin.math.sin(radians)
            return MutableMatrix(
                m00 = c, m01 = -s, m02 = 0.0, m03 = 0.0,
                m10 = s, m11 = c, m12 = 0.0, m13 = 0.0,
                m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = 0.0,
                m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
            )
        }

        fun ofRotation(
            aX: Double,
            aY: Double,
            aZ: Double,
        ): Matrix {
            val cX = kotlin.math.cos(aX)
            val cY = kotlin.math.cos(aY)
            val cZ = kotlin.math.cos(aZ)
            val sX = kotlin.math.sin(aX)
            val sY = kotlin.math.sin(aY)
            val sZ = kotlin.math.sin(aZ)
            return MutableMatrix(
                m00 = cZ * cY, m01 = cZ * sY * sX - sZ * cX, m02 = cZ * sY * cX + sZ * sX, m03 = 0.0,
                m10 = sZ * cY, m11 = sZ * sY * sX + cZ * cX, m12 = sZ * sY * cX - cZ * sX, m13 = 0.0,
                m20 = -sY, m21 = cY * sX, m22 = cY * cX, m23 = 0.0,
                m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
            )
        }

        fun ofTranslation(
            dX: Double,
            dY: Double,
            dZ: Double,
        ): Matrix {
            return MutableMatrix(
                m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = dX,
                m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = dY,
                m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = dZ,
                m30 = 0.0, m31 = 0.0, m32 = 0.0, m33 = 1.0,
            )
        }
    }
}

@Deprecated("sp.kx.math.identity")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
) {
    m00 = 1.0; m01 = 0.0; m02 = 0.0; m03 = dX
    m10 = 0.0; m11 = 1.0; m12 = 0.0; m13 = dY
    m20 = 0.0; m21 = 0.0; m22 = 1.0; m23 = dZ
    m30 = 0.0; m31 = 0.0; m32 = 0.0; m33 = 1.0
}

@Deprecated("sp.kx.math.identity")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
    pointOfRotation: Point3D,
    aX: Double,
) {
    identity()
//    mul(MutableMatrix.ofTranslation(dX = dX, dY = dY, dZ = dZ))
    mul(o03 = dX, o13 = dY, o23 = dZ)
//    mul(MutableMatrix.ofTranslation(dX = -pointOfRotation.x, dY = -pointOfRotation.y, dZ = -pointOfRotation.z))
    mul(o03 = -pointOfRotation.x, o13 = -pointOfRotation.y, o23 = -pointOfRotation.z)
    mul(MutableMatrix.ofRotationX(radians = aX))
//    mul(MutableMatrix.ofTranslation(dX = pointOfRotation.x, dY = pointOfRotation.y, dZ = pointOfRotation.z))
    mul(o03 = pointOfRotation.x, o13 = pointOfRotation.y, o23 = pointOfRotation.z)
}

@Deprecated("sp.kx.math.identity")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
    rX: Double,
    rY: Double,
    rZ: Double,
    aX: Double,
    aY: Double,
    aZ: Double,
) {
    identity()
    mul(o03 = dX, o13 = dY, o23 = dZ)
    mul(o03 = -rX, o13 = -rY, o23 = -rZ)
    mul(MutableMatrix.ofRotationZ(radians = aZ))
    mul(MutableMatrix.ofRotationY(radians = aY))
    mul(MutableMatrix.ofRotationX(radians = aX))
//    mul(MutableMatrix.ofRotation(aX = aX, aY = aY, aZ = aZ))
    mul(o03 = rX, o13 = rY, o23 = rZ)
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

@Deprecated("sp.kx.math.rotateX")
internal fun MutableMatrix.rotateX(oY: Double, oZ: Double, radians: Double) {
//    translate(dX = -oX, dY = 0.0, dZ = -oZ)
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    m11 = c
    m12 = -s
    m21 = s
    m22 = c
//    translate(dX = oX, dY = 0.0, dZ = oZ)
}

@Deprecated("sp.kx.math.rotateX")
internal fun MutableMatrix.rotateX(oX: Double, oY: Double, oZ: Double, radians: Double) {
    translate(dX = -oX, dY = 0.0, dZ = -oZ)
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    m11 = c
    m12 = -s
    m21 = s
    m22 = c
    translate(dX = oX, dY = 0.0, dZ = oZ)
}

@Deprecated("sp.kx.math.rotateY")
internal fun MutableMatrix.rotateY(oX: Double, oZ: Double, radians: Double) {
//    translate(dX = -oX, dY = 0.0, dZ = -oZ)
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    m00 = c
    m02 = s
    m20 = -s
    m22 = c
//    translate(dX = oX, dY = 0.0, dZ = oZ)
}

@Deprecated("sp.kx.math.translate")
internal fun MutableMatrix.translate(dX: Double, dY: Double, dZ: Double) {
    m03 = dX
    m13 = dY
    m23 = dZ
}

@Deprecated("sp.kx.math.scale")
internal fun MutableMatrix.scale(dX: Double, dY: Double, dZ: Double) {
    m00 = dX
    m11 = dY
    m22 = dZ
}

@Deprecated("sp.kx.math.translate")
internal fun MutableMatrix.mul(other: Matrix) {
    val m1 = mut()
    m00 = m1.m00 * other.m00 + m1.m01 * other.m10 + m1.m02 * other.m20 + m1.m03 * other.m30
    m01 = m1.m00 * other.m01 + m1.m01 * other.m11 + m1.m02 * other.m21 + m1.m03 * other.m31
    m02 = m1.m00 * other.m02 + m1.m01 * other.m12 + m1.m02 * other.m22 + m1.m03 * other.m32
    m03 = m1.m00 * other.m03 + m1.m01 * other.m13 + m1.m02 * other.m23 + m1.m03 * other.m33
    //
    m10 = m1.m10 * other.m00 + m1.m11 * other.m10 + m1.m12 * other.m20 + m1.m13 * other.m30
    m11 = m1.m10 * other.m01 + m1.m11 * other.m11 + m1.m12 * other.m21 + m1.m13 * other.m31
    m12 = m1.m10 * other.m02 + m1.m11 * other.m12 + m1.m12 * other.m22 + m1.m13 * other.m32
    m13 = m1.m10 * other.m03 + m1.m11 * other.m13 + m1.m12 * other.m23 + m1.m13 * other.m33
    //
    m20 = m1.m20 * other.m00 + m1.m21 * other.m10 + m1.m22 * other.m20 + m1.m23 * other.m30
    m21 = m1.m20 * other.m01 + m1.m21 * other.m11 + m1.m22 * other.m21 + m1.m23 * other.m31
    m22 = m1.m20 * other.m02 + m1.m21 * other.m12 + m1.m22 * other.m22 + m1.m23 * other.m32
    m23 = m1.m20 * other.m03 + m1.m21 * other.m13 + m1.m22 * other.m23 + m1.m23 * other.m33
    //
    m30 = m1.m30 * other.m00 + m1.m31 * other.m10 + m1.m32 * other.m20 + m1.m33 * other.m30
    m31 = m1.m30 * other.m01 + m1.m31 * other.m11 + m1.m32 * other.m21 + m1.m33 * other.m31
    m32 = m1.m30 * other.m02 + m1.m31 * other.m12 + m1.m32 * other.m22 + m1.m33 * other.m32
    m33 = m1.m30 * other.m03 + m1.m31 * other.m13 + m1.m32 * other.m23 + m1.m33 * other.m33
}

@Deprecated("sp.kx.math.translate")
internal fun MutableMatrix.mul(o03: Double, o13: Double, o23: Double) {
    m03 += m00 * o03 + m01 * o13 + m02 * o23
    m13 += m10 * o03 + m11 * o13 + m12 * o23
    m23 += m20 * o03 + m21 * o13 + m22 * o23
    m33 += m30 * o03 + m31 * o13 + m32 * o23
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

@Deprecated("sp.kx.math.rotatedY")
internal fun Point3D.rotatedY(oX: Double, oZ: Double, radians: Double): Point3D {
    val distance = distanceOf(aX = oX, aY = oZ, bX = x, bY = z)
    val angle = angleOf(aX = oX, aY = oZ, bX = x, bY = z)
    return MutablePoint3D(
        x = oX + distance * kotlin.math.cos(angle + radians),
        y = y,
        z = oZ + distance * kotlin.math.sin(angle + radians),
    )
}

@Deprecated("sp.kx.math.rotatedY")
internal fun Point3D.translated(dX: Double, dY: Double, dZ: Double): Point3D {
    return MutablePoint3D(
        x = this.x + dX,
        y = this.y + dY,
        z = this.z + dZ,
    )
}

@Deprecated("sp.kx.math.MutablePoint")
internal class MutablePoint3D(
    override var x: Double,
    override var y: Double,
    override var z: Double,
) : Point3D {
    fun translate(dX: Double, dY: Double, dZ: Double) {
        this.x += dX
        this.y += dY
        this.z += dZ
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

    fun rotateX(oY: Double, oZ: Double, radians: Double) {
        val distance = distanceOf(aX = oZ, aY = oY, bX = z, bY = y)
        val angle = angleOf(aX = oZ, aY = oY, bX = z, bY = y)
        z = oZ + distance * kotlin.math.cos(angle + radians)
        y = oY + distance * kotlin.math.sin(angle + radians)
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

    fun rotateY1(oX: Double, oZ: Double, radians: Double) {
        x -= oX
        z -= oZ
        // [ c 0 s ] [ x ] = [ c * x + 0 * x + s * x ]   [ c * x + s * x ]
        // [ 0 1 0 ] [ y ] = [ 0 * y + 1 * y + 0 * y ] = [ y ]
        // [-s 0 c ] [ z ] = [-s * z + 0 * z + c * z ]   [-s * z + c * z ]
        val c = kotlin.math.cos(radians)
        val s = kotlin.math.sin(radians)
        x = c * x + s * x
        z = c * z - s * z
        //
        x += oX
        z += oZ
    }

    fun rotateY2(oX: Double, oZ: Double, radians: Double) {
        val distance = distanceOf(aX = oX, aY = oZ, bX = x, bY = z)
        val angle = angleOf(aX = oX, aY = oZ, bX = x, bY = z)
        x = oX + distance * kotlin.math.cos(angle + radians)
        z = oZ + distance * kotlin.math.sin(angle + radians)
    }

    fun rotateY(oX: Double, oZ: Double, radians: Double) {
        x -= oX
        z -= oZ
        val c = kotlin.math.cos(radians)
        val s = kotlin.math.sin(radians)
        x = z * s + x * c
        z = z * c - x * s
        x += oX
        z += oZ
    }

    fun rotateZ(point: Point3D, radians: Double) {
        val distance = distanceOf(aX = point.x, aY = point.y, bX = x, bY = y)
        val angle = angleOf(aX = point.x, aY = point.y, bX = x, bY = y)
        x = point.x + distance * kotlin.math.cos(angle + radians)
        y = point.y + distance * kotlin.math.sin(angle + radians)
    }

    fun mul(matrix: Matrix) {
        val oX = x
        val oY = y
        val oZ = z
        x = matrix.m00 * oX + matrix.m01 * oY + matrix.m02 * oZ + matrix.m03
        y = matrix.m10 * oX + matrix.m11 * oY + matrix.m12 * oZ + matrix.m13
        z = matrix.m20 * oX + matrix.m21 * oY + matrix.m22 * oZ + matrix.m23
    }
}
