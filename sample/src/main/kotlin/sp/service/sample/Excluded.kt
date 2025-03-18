package sp.service.sample

import sp.kx.calculations.Size
import sp.kx.calculations.algebra.Matrix
import sp.kx.calculations.algebra.MutableMatrix
import sp.kx.calculations.algebra.identity
import sp.kx.calculations.algebra.translate
import sp.kx.calculations.geometry.MutableOffset
import sp.kx.calculations.geometry.MutableVertex
import sp.kx.calculations.geometry.Offset
import sp.kx.calculations.geometry.Vertex
import sp.kx.calculations.operators.times
import sp.kx.calculations.operators.timesAssign
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Deprecated("sp.kx.calculations.transpose")
internal fun MutableMatrix.transpose() {
    val m01 = this.m10; val m02 = this.m20; val m03 = this.m30
    val m10 = this.m01; val m12 = this.m21; val m13 = this.m31
    val m20 = this.m02; val m21 = this.m12; val m23 = this.m32
    val m30 = this.m03; val m31 = this.m13; val m32 = this.m23
    //
    this.m01 = m01; this.m02 = m02; this.m03 = m03
    this.m10 = m10; this.m12 = m12; this.m13 = m13
    this.m20 = m20; this.m21 = m21; this.m23 = m23
    this.m30 = m30; this.m31 = m31; this.m32 = m32
}

@Deprecated("sp.kx.calculations.translate")
internal fun MutableMatrix.translate(offset: Offset) {
    m03 += m00 * offset.dX + m01 * offset.dY + m02 * offset.dZ
    m13 += m10 * offset.dX + m11 * offset.dY + m12 * offset.dZ
    m23 += m20 * offset.dX + m21 * offset.dY + m22 * offset.dZ
    m33 += m30 * offset.dX + m31 * offset.dY + m32 * offset.dZ
}

@Deprecated("sp.kx.calculations.times")
internal operator fun Offset.times(scale: Double): Offset {
    return MutableOffset(
        dX = dX * scale,
        dY = dY * scale,
        dZ = dZ * scale,
    )
}

@Deprecated("sp.kx.calculations.div")
internal operator fun Offset.div(scale: Double): Offset {
    return MutableOffset(
        dX = dX / scale,
        dY = dY / scale,
        dZ = dZ / scale,
    )
}

@Deprecated("sp.kx.calculations.plus")
internal operator fun Offset.plus(other: Offset): Offset {
    return MutableOffset(
        dX = dX + other.dX,
        dY = dY + other.dY,
        dZ = dZ + other.dZ,
    )
}

@Deprecated("sp.kx.calculations.minus")
internal operator fun Offset.minus(other: Offset): Offset {
    return MutableOffset(
        dX = dX - other.dX,
        dY = dY - other.dY,
        dZ = dZ - other.dZ,
    )
}

@Deprecated("sp.kx.calculations.pov")
internal fun pov(
    pictureSize: Size,
    offset: Offset,
): Vertex {
    return MutableVertex(
        x = pictureSize.width / 2 - offset.dX,
        y = pictureSize.height / 2 - offset.dY,
        z = offset.dZ,
    )
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
    aX: Double,
    aY: Double,
    aZ: Double,
) {
    identity()
    translate(dX = dX, dY = dY, dZ = dZ)
//    mul(MutableMatrix.ofRotationQ(rX = 1.0, rY = 0.0, rZ = 0.0, radians = aX))
//    mul(MutableMatrix.ofRotationQ(rX = 0.0, rY = 1.0, rZ = 0.0, radians = aY))
//    mul(MutableMatrix.ofRotationQ(rX = 0.0, rY = 0.0, rZ = 1.0, radians = aZ))
    val qX = MutableQuaternion.ofVector(x = 1.0, y = 0.0, z = 0.0, radians = aX / 2)
    val qY = MutableQuaternion.ofVector(x = 0.0, y = 1.0, z = 0.0, radians = aY / 2)
    val qZ = MutableQuaternion.ofVector(x = 0.0, y = 0.0, z = 1.0, radians = aZ / 2)
    val q = qX * qY * qZ
    timesAssign(Matrices.ofQuaternion(q))
}

/*
@Deprecated("sp.kx.math.ortho")
internal fun MutableMatrix.ortho(l: Double, t: Double, r: Double, b: Double, n: Double, f: Double) {
    m00 = 2.0 / (r - l)
    m11 = 2.0 / (t - b)
    m22 = 2.0 / (n - f)
    m30 = (r + l) / (l - r)
    m31 = (t + b) / (b - t)
    m32 = (f + n) / (n - f)
}
*/

@Deprecated("sp.kx.math.ortho")
internal fun MutableMatrix.ortho(l: Double, t: Double, r: Double, b: Double, n: Double, f: Double) {
    val a00 = 2.0 / (r - l); val a01 = 0.0;           val a02 = 0.0;           val a03 = (l + r) / (l - r)
    val a10 = 0.0;           val a11 = 2.0 / (t - b); val a12 = 0.0;           val a13 = (b + t) / (b - t)
    val a20 = 0.0;           val a21 = 0.0;           val a22 = 2.0 / (n - f); val a23 = (n + f) / (n - f)
    val a30 = 0.0;           val a31 = 0.0;           val a32 = 0.0;           val a33 = 1.0
    //
    val m00 = this.m00 * a00 + this.m01 * a10 + this.m02 * a20 + this.m03 * a30
    val m01 = this.m00 * a01 + this.m01 * a11 + this.m02 * a21 + this.m03 * a31
    val m02 = this.m00 * a02 + this.m01 * a12 + this.m02 * a22 + this.m03 * a32
    val m03 = this.m00 * a03 + this.m01 * a13 + this.m02 * a23 + this.m03 * a33
    val m10 = this.m10 * a00 + this.m11 * a10 + this.m12 * a20 + this.m13 * a30
    val m11 = this.m10 * a01 + this.m11 * a11 + this.m12 * a21 + this.m13 * a31
    val m12 = this.m10 * a02 + this.m11 * a12 + this.m12 * a22 + this.m13 * a32
    val m13 = this.m10 * a03 + this.m11 * a13 + this.m12 * a23 + this.m13 * a33
    val m20 = this.m20 * a00 + this.m21 * a10 + this.m22 * a20 + this.m23 * a30
    val m21 = this.m20 * a01 + this.m21 * a11 + this.m22 * a21 + this.m23 * a31
    val m22 = this.m20 * a02 + this.m21 * a12 + this.m22 * a22 + this.m23 * a32
    val m23 = this.m20 * a03 + this.m21 * a13 + this.m22 * a23 + this.m23 * a33
    val m30 = this.m30 * a00 + this.m31 * a10 + this.m32 * a20 + this.m33 * a30
    val m31 = this.m30 * a01 + this.m31 * a11 + this.m32 * a21 + this.m33 * a31
    val m32 = this.m30 * a02 + this.m31 * a12 + this.m32 * a22 + this.m33 * a32
    val m33 = this.m30 * a03 + this.m31 * a13 + this.m32 * a23 + this.m33 * a33
    //
    this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03
    this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13
    this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23
    this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33
}

/*
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
*/

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
