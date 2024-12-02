package sp.service.sample.util

import org.json.JSONObject
import sp.kx.math.Point
import sp.kx.math.Vector
import sp.kx.math.plus
import sp.kx.math.pointOf
import sp.service.sample.entity.Barrier
import sp.service.sample.entity.Condition
import sp.service.sample.entity.Crate
import sp.service.sample.entity.Item
import sp.service.sample.entity.Position
import sp.service.sample.entity.Relay
import sp.service.sample.entity.Tag
import java.util.UUID

internal fun JSONObject.toCondition(): Condition {
    return Condition(
        id = UUID.fromString(getString("id")),
        passed = optBoolean("passed", false),
    )
}

internal fun JSONObject.toRelay(): Relay {
    val required: Relay.Required? = getJSONObjectOrNull("required")?.let { obj ->
        val type = Relay.Required.Type.valueOf(obj.getString("type"))
        val itemsTags = obj.strings("itemsTags", UUID::fromString)
        Relay.Required(
            type = type,
            itemsTags = itemsTags,
        )
    }
    return Relay(
        id = UUID.fromString(getString("id")),
        point = getJSONObject("point").toPoint(),
        required = required,
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
    val tags = getJSONArray("tags").let { array ->
        (0 until array.length()).map { index ->
            array.getJSONArray(index).let { ids ->
                (0 until ids.length()).map {
                    UUID.fromString(ids.getString(it))
                }
            }
        }
    }
    return Barrier(
        id = UUID.fromString(getString("id")),
        vector = getJSONObject("vector").toVector(),
        opened = getBoolean("opened"),
        tags = tags,
    )
}

internal fun JSONObject.toTag(): Tag {
    return Tag(
        id = UUID.fromString(getString("id")),
    )
}

internal fun JSONObject.toPosition(): Position {
    return Position(
        id = UUID.fromString(getString("id")),
        point = getJSONObject("point").toPoint(),
    )
}

internal fun JSONObject.toCrate(): Crate {
    return Crate(
        id = UUID.fromString(getString("id")),
        point = getJSONObject("point").toPoint(),
    )
}
