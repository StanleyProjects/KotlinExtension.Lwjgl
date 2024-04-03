package sp.service.sample.util

import org.json.JSONArray
import org.json.JSONObject

internal fun <T : Any> JSONArray.map(transform: (index: Int, JSONArray) -> T): List<T> {
    return (0 until length()).map { index ->
        transform(index, this)
    }
}

internal fun <T : Any> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> {
    return map { index, _ ->
        transform(getJSONObject(index))
    }
}

internal fun <T : Any> JSONObject.objects(name: String, transform: (JSONObject) -> T): List<T> {
    if (!has(name) || isNull(name)) return emptyList()
    return getJSONArray(name).mapObjects(transform)
}

internal fun <T : Any> JSONArray.mapStrings(transform: (String) -> T): List<T> {
    return map { index, _ ->
        transform(getString(index))
    }
}

internal fun <T : Any> JSONObject.strings(name: String, transform: (String) -> T): List<T> {
    if (!has(name) || isNull(name)) return emptyList()
    return getJSONArray(name).mapStrings(transform)
}

internal fun <K : Any, V : Any> JSONObject.toMap(
    keys: (String) -> K,
    values: (String, JSONObject) -> V,
): Map<K, V> {
    return keys().asSequence().map { name ->
        keys(name) to values(name, this)
    }.toMap()
}

internal fun <K : Any, V : Any> JSONObject.toMapStrings(
    keys: (String) -> K,
    values: (String) -> V,
): Map<K, V> {
    return toMap(
        keys = keys,
        values = { name, obj -> values(obj.getString(name)) }
    )
}

internal fun JSONObject.getStringOrNull(key: String): String? {
    if (!has(key)) return null
    if (isNull(key)) return null
    return getString(key)
}

internal fun JSONObject.getJSONObjectOrNull(key: String): JSONObject? {
    if (!has(key)) return null
    if (isNull(key)) return null
    return getJSONObject(key)
}
