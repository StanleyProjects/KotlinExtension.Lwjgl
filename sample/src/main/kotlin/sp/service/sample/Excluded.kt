package sp.service.sample

import sp.kx.math.Matrix
import sp.kx.math.Measure
import sp.kx.math.MutableMatrix
import sp.kx.math.MutableOffset
import sp.kx.math.MutableSize
import sp.kx.math.MutableVertex
import sp.kx.math.Offset
import sp.kx.math.Rotation
import sp.kx.math.Size
import sp.kx.math.Vertex
import sp.kx.math.timesAssign
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Deprecated("sp.kx.math.IntPoint")
internal interface IntPoint {
    val x: Int
    val y: Int
}

@Deprecated("sp.kx.math.MutableIntPoint")
internal class MutableIntPoint(
    override var x: Int,
    override var y: Int,
) : IntPoint

@Deprecated("sp.kx.math.isEmpty")
internal fun Offset.isEmpty(): Boolean {
    return dX == 0.0 && dY == 0.0 && dZ == 0.0
}

@Deprecated("sp.kx.math.angleOf")
internal fun angleOf(
    aX: Double,
    aY: Double,
    bX: Double,
    bY: Double,
): Double {
    return kotlin.math.atan2(y = bY - aY, x = bX - aX)
}

@Deprecated("sp.kx.math.angleOf")
internal fun angleOf(
    x: Double,
    y: Double,
): Double {
    return kotlin.math.atan2(y = y, x = x)
}

@Deprecated("sp.kx.math.length")
internal fun length(magnitude: Double, timeUnit: TimeUnit, duration: Duration): Double {
    val raw = magnitude / timeUnit.toNanos(1)
    return raw * duration.inWholeNanoseconds
}

@Deprecated("sp.kx.math.Quaternion")
internal interface Quaternion {
    val s: Double
    val x: Double
    val y: Double
    val z: Double
}

@Deprecated("sp.kx.math.set")
internal fun MutableMatrix.set(q: Quaternion) {
    val x2  = q.x + q.x
    val y2  = q.y + q.y
    val z2  = q.z + q.z
    val xx2 = q.x * x2
    val xy2 = q.x * y2
    val xz2 = q.x * z2
    val yy2 = q.y * y2
    val yz2 = q.y * z2
    val zz2 = q.z * z2
    val sx2 = q.s * x2
    val sy2 = q.s * y2
    val sz2 = q.s * z2
    m00 = 1 - (yy2 + zz2); m01 = xy2 + sz2;       m02 = xz2 - sy2;       m03 = 0.0
    m10 = xy2 - sz2;       m11 = 1 - (xx2 + zz2); m12 = yz2 + sx2;       m13 = 0.0
    m20 = xz2 + sy2;       m21 = yz2 - sx2;       m22 = 1 - (xx2 + yy2); m23 = 0.0
    m30 = 0.0;             m31 = 0.0;             m32 = 0.0;             m33 = 1.0
}

@Deprecated("sp.kx.math.mut")
internal fun Quaternion.mut(): MutableQuaternion {
    return MutableQuaternion(
        s = s,
        x = x,
        y = y,
        z = z,
    )
}

@Deprecated("sp.kx.math.rotated")
internal fun Vertex.rotated(q: Quaternion): Vertex {
    val p = MutableQuaternion.ofVector(x = x, y = y, z = z)
    val c = q.mut()
    c.conjugate()
    val r = q * p * c
    return MutableVertex(
        x = r.x,
        y = r.y,
        z = r.z,
    )
}

@Deprecated("sp.kx.math.cross")
internal fun Vertex.cross(other: Vertex): Vertex {
    return MutableVertex(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x,
    )
}

@Deprecated("sp.kx.math.plus")
internal operator fun Vertex.plus(other: Vertex): Vertex {
    return MutableVertex(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )
}

@Deprecated("sp.kx.math.times")
internal operator fun Vertex.times(value: Double): Vertex {
    return MutableVertex(
        x = x * value,
        y = y * value,
        z = z * value,
    )
}

@Deprecated("sp.kx.math.dot")
internal fun Vertex.dot(other: Vertex): Double {
    return x * other.x + y * other.y + z * other.z
}

@Deprecated("sp.kx.math.times")
internal operator fun Quaternion.times(other: Quaternion): Quaternion {
    val v1 = MutableVertex(x = x, y = y, z = z)
    val v2 = MutableVertex(x = other.x, y = other.y, z = other.z)
    val vc = v1.cross(v2)
    val d = v1.dot(v2)
    val v3 = vc + (v2 * s) + (v1 * other.s)
    return MutableQuaternion(
        s = s * other.s - d,
        x = v3.x,
        y = v3.y,
        z = v3.z,
    )
}

@Deprecated("sp.kx.math.MutableQuaternion")
internal class MutableQuaternion(
    override var s: Double,
    override var x: Double,
    override var y: Double,
    override var z: Double
) : Quaternion {
    fun conjugate() {
        x = -x
        y = -y
        z = -z
    }

    companion object {
        fun ofVector(x: Double, y: Double, z: Double): MutableQuaternion {
            return MutableQuaternion(
                s = 0.0, x = x, y = y, z = z,
            )
        }

        fun ofVector(x: Double, y: Double, z: Double, radians: Double): MutableQuaternion {
            val s = kotlin.math.sin(radians)
            return MutableQuaternion(
                s = kotlin.math.cos(radians),
                x = x * s,
                y = y * s,
                z = z * s,
            )
        }
    }
}

internal object Matrices {
    fun of(other: Matrix): MutableMatrix {
        return MutableMatrix(
            m00 = other.m00, m01 = other.m01, m02 = other.m02, m03 = other.m03,
            m10 = other.m10, m11 = other.m11, m12 = other.m12, m13 = other.m13,
            m20 = other.m20, m21 = other.m21, m22 = other.m22, m23 = other.m23,
            m30 = other.m30, m31 = other.m31, m32 = other.m32, m33 = other.m33,
        )
    }

    fun ofQuaternion(q: Quaternion): MutableMatrix {
        val x2  = q.x + q.x
        val y2  = q.y + q.y
        val z2  = q.z + q.z
        val xx2 = q.x * x2
        val xy2 = q.x * y2
        val xz2 = q.x * z2
        val yy2 = q.y * y2
        val yz2 = q.y * z2
        val zz2 = q.z * z2
        val sx2 = q.s * x2
        val sy2 = q.s * y2
        val sz2 = q.s * z2
        return MutableMatrix(
            m00 = 1 - (yy2 + zz2), m01 = xy2 + sz2,       m02 = xz2 - sy2,       m03 = 0.0,
            m10 = xy2 - sz2,       m11 = 1 - (xx2 + zz2), m12 = yz2 + sx2,       m13 = 0.0,
            m20 = xz2 + sy2,       m21 = yz2 - sx2,       m22 = 1 - (xx2 + yy2), m23 = 0.0,
            m30 = 0.0,             m31 = 0.0,             m32 = 0.0,             m33 = 1.0,
        )
    }

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
            m20 = -sY,     m21 = cY * sX,                m22 = cY * cX,                m23 = 0.0,
            m30 = 0.0,     m31 = 0.0,                    m32 = 0.0,                    m33 = 1.0,
        )
    }

    fun ofRotation(
        rX: Double,
        rY: Double,
        rZ: Double,
        radians: Double,
    ): Matrix {
        val c = kotlin.math.cos(radians)
        val s = kotlin.math.sin(radians)
        return MutableMatrix(
            m00 = c + (1 - c) * rX * rX,      m01 = (1 - c) * rX * rY - s * rZ, m02 = (1 - c) * rX * rZ + s * rY, m03 = 0.0,
            m10 = (1 - c) * rY * rX + s * rZ, m11 = c + (1 - c) * rY * rY,      m12 = (1 - c) * rY * rZ - s * rX, m13 = 0.0,
            m20 = (1 - c) * rZ * rX - s * rY, m21 = (1 - c) * rZ * rY + s * rX, m22 = c + (1 - c) * rZ * rZ,      m23 = 0.0,
            m30 = 0.0,                        m31 = 0.0,                        m32 = 0.0,                        m33 = 1.0,
        )
    }

    fun ofRotationQ(
        rX: Double,
        rY: Double,
        rZ: Double,
        radians: Double,
    ): Matrix {
//            val s = kotlin.math.cos(radians / 2)
//            val x = rX * kotlin.math.sin(radians / 2)
//            val y = rY * kotlin.math.sin(radians / 2)
//            val z = rZ * kotlin.math.sin(radians / 2)
//            return MutableMatrix(
//                m00 = 1 - 2 * y * y - 2 * z * z, m01 = 2 * x * y - 2 * s * z,     m02 = 2 * x * z + 2 * s * y,     m03 = 0.0,
//                m10 = 2 * x * y + 2 * s * z,     m11 = 1 - 2 * x * x - 2 * z * z, m12 = 2 * y * z - 2 * s * x,     m13 = 0.0,
//                m20 = 2 * x * z - 2 * s * y,     m21 = 2 * y * z + 2 * s * x,     m22 = 1 - 2 * x * x - 2 * y * y, m23 = 0.0,
//                m30 = 0.0,                       m31 = 0.0,                       m32 = 0.0,                       m33 = 0.0,
//            )
        val q = MutableQuaternion.ofVector(x = rX, y = rY, z = rZ, radians = radians / 2)
        val matrix = MutableMatrix()
        matrix.set(q)
        return matrix
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

@Deprecated("sp.kx.math.perform")
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

@Deprecated("sp.kx.math.perform")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
    pointOfRotation: Vertex,
    aX: Double,
) {
    identity()
//    mul(MutableMatrix.ofTranslation(dX = dX, dY = dY, dZ = dZ))
    mul(o03 = dX, o13 = dY, o23 = dZ)
//    mul(MutableMatrix.ofTranslation(dX = -pointOfRotation.x, dY = -pointOfRotation.y, dZ = -pointOfRotation.z))
    mul(o03 = -pointOfRotation.x, o13 = -pointOfRotation.y, o23 = -pointOfRotation.z)
    timesAssign(Matrices.ofRotationX(radians = aX))
//    mul(MutableMatrix.ofTranslation(dX = pointOfRotation.x, dY = pointOfRotation.y, dZ = pointOfRotation.z))
    mul(o03 = pointOfRotation.x, o13 = pointOfRotation.y, o23 = pointOfRotation.z)
}

@Deprecated("sp.kx.math.perform")
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
//    mul(o03 = -rX, o13 = -rY, o23 = -rZ)
//    mul(MutableMatrix.ofRotationZ(radians = aZ))
//    mul(MutableMatrix.ofRotationY(radians = aY))
//    mul(MutableMatrix.ofRotationX(radians = aX))
    timesAssign(Matrices.ofRotation(aX = aX, aY = aY, aZ = aZ))
//    mul(o03 = rX, o13 = rY, o23 = rZ)
}

@Deprecated("sp.kx.math.perform")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
    rX: Double,
    rY: Double,
    rZ: Double,
    radians: Double,
) {
    identity()
    mul(o03 = dX, o13 = dY, o23 = dZ)
    timesAssign(Matrices.ofRotation(rX = rX, rY = rY, rZ = rZ, radians = radians))
}

@Deprecated("sp.kx.math.perform")
internal fun MutableMatrix.perform(
    dX: Double,
    dY: Double,
    dZ: Double,
    aX: Double,
    aY: Double,
    aZ: Double,
) {
    identity()
    mul(o03 = dX, o13 = dY, o23 = dZ)
//    mul(MutableMatrix.ofRotationQ(rX = 1.0, rY = 0.0, rZ = 0.0, radians = aX))
//    mul(MutableMatrix.ofRotationQ(rX = 0.0, rY = 1.0, rZ = 0.0, radians = aY))
//    mul(MutableMatrix.ofRotationQ(rX = 0.0, rY = 0.0, rZ = 1.0, radians = aZ))
    val qX = MutableQuaternion.ofVector(x = 1.0, y = 0.0, z = 0.0, radians = aX / 2)
    val qY = MutableQuaternion.ofVector(x = 0.0, y = 1.0, z = 0.0, radians = aY / 2)
    val qZ = MutableQuaternion.ofVector(x = 0.0, y = 0.0, z = 1.0, radians = aZ / 2)
    val q = qX * qY * qZ
    timesAssign(Matrices.ofQuaternion(q))
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

@Deprecated("sp.kx.math.mul")
internal fun MutableMatrix.mul(o03: Double, o13: Double, o23: Double) {
    m03 += m00 * o03 + m01 * o13 + m02 * o23
    m13 += m10 * o03 + m11 * o13 + m12 * o23
    m23 += m20 * o03 + m21 * o13 + m22 * o23
    m33 += m30 * o03 + m31 * o13 + m32 * o23
}

@Deprecated("sp.kx.math.copy")
internal fun Vertex.copy(x: Double = this.x, y: Double = this.y, z: Double = this.z): Vertex {
    return MutableVertex(
        x = x,
        y = y,
        z = z,
    )
}

@Deprecated("sp.kx.math.rotatedX")
internal fun Vertex.rotatedX(radians: Double): Vertex {
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    return MutableVertex(
        x = x,
        y = y * c - z * s,
        z = y * s + z * c,
    )
}

@Deprecated("sp.kx.math.rotatedY")
internal fun Vertex.rotatedY(radians: Double): Vertex {
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    return MutableVertex(
        x = x * c - z * s,
        y = y,
        z = x * s + z * c,
    )
}

@Deprecated("sp.kx.math.rotatedZ")
internal fun Vertex.rotatedZ(radians: Double): Vertex {
    val c = kotlin.math.cos(radians)
    val s = kotlin.math.sin(radians)
    return MutableVertex(
        x = x * c - y * s,
        y = x * s + y * c,
        z = z,
    )
}

@Deprecated("sp.kx.math.rotated")
internal fun Vertex.rotated(rotation: Rotation): Vertex {
    var c = kotlin.math.cos(rotation.aX)
    var s = kotlin.math.sin(rotation.aX)
    var x = x
    val y = y * c - z * s
    var z = y * s + z * c
    c = kotlin.math.cos(rotation.aY)
    s = kotlin.math.sin(rotation.aY)
    x = x * c - z * s
    z = x * s + z * c
    c = kotlin.math.cos(rotation.aZ)
    s = kotlin.math.sin(rotation.aZ)
    return MutableVertex(
        x = x * c - y * s,
        y = x * s + y * c,
        z = z,
    )
}
