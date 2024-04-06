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
import sp.service.sample.entity.ItemPosition
import sp.service.sample.entity.ItemTag
import sp.service.sample.entity.Relay
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
    return Barrier(
        id = UUID.fromString(getString("id")),
        vector = getJSONObject("vector").toVector(),
    )
}

internal fun JSONObject.toItem(): Item {
    return Item(
        id = UUID.fromString(getString("id")),
        tags = strings("tags", UUID::fromString),
    )
}

internal fun JSONObject.toItemTag(): ItemTag {
    return ItemTag(
        id = UUID.fromString(getString("id")),
    )
}

internal fun JSONObject.toItemPosition(): ItemPosition {
    return ItemPosition(
        id = UUID.fromString(getString("id")),
        itemId = UUID.fromString(getString("itemId")),
        point = getJSONObject("point").toPoint(),
    )
}

internal fun JSONObject.toCrate(): Crate {
    return Crate(
        id = UUID.fromString(getString("id")),
        point = getJSONObject("point").toPoint(),
    )
}
