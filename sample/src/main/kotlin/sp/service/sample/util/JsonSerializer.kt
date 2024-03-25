package sp.service.sample.util

import org.json.JSONObject
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.service.sample.entity.Barrier
import sp.service.sample.entity.Condition
import sp.service.sample.entity.Relay
import java.util.UUID

internal fun JSONObject.toCondition(): Condition {
    return Condition(
        id = UUID.fromString(getString("id")),
        passed = optBoolean("passed", false),
    )
}

internal fun JSONObject.toRelay(): Relay {
    return Relay(
        id = UUID.fromString(getString("id")),
        enabled = optBoolean("enabled", false),
        point = getJSONObject("point").toPoint(),
    )
}

internal fun JSONObject.toPoint(): Point {
    return pointOf(
        x = getDouble("x"),
        y = getDouble("y"),
    )
}

internal fun JSONObject.toVector(): Vector {
    return getJSONObject("start").toPoint() + getJSONObject("finish").toPoint()
}

internal fun JSONObject.toBarrier(): Barrier {
    return Barrier(
        id = UUID.fromString(getString("id")),
        vector = getJSONObject("vector").toVector(),
    )
}
