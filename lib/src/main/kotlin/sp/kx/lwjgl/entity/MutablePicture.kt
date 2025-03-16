package sp.kx.lwjgl.entity

import sp.kx.calculations.MutableSize
import sp.kx.calculations.Size
import sp.kx.calculations.geometry.MutableOffset
import sp.kx.calculations.geometry.Offset

class MutablePicture(
    width: Double,
    height: Double,
) : Picture {
    override val size: Size get() {
        return _size
    }

    override val center: Offset get() {
        return _center
    }

    private val _size = MutableSize(width = width, height = height)
    private val _center = MutableOffset(dX = size.width / 2.0, dY = size.height / 2.0, dZ = 0.0)

    fun set(
        width: Double,
        height: Double,
    ) {
        _size.width = width
        _size.height = height
        _center.dX = width / 2.0
        _center.dY = height / 2.0
    }

    fun set(size: Size) {
        _size.width = size.width
        _size.height = size.height
        _center.dX = size.width / 2.0
        _center.dY = size.height / 2.0
    }

    companion object {
        fun of(size: Size?): MutablePicture {
            if (size == null) return MutablePicture(width = Double.NaN, height = Double.NaN)
            return MutablePicture(width = size.width, height = size.height)
        }
    }
}
