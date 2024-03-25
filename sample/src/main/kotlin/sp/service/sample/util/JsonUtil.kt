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
    if (!has(name)) return emptyList()
    return getJSONArray(name).mapObjects(transform)
}

internal fun <T : Any> JSONArray.mapStrings(transform: (String) -> T): List<T> {
    return map { index, _ ->
        transform(getString(index))
    }
}

internal fun <T : Any> JSONObject.strings(name: String, transform: (String) -> T): List<T> {
    if (!has(name)) return emptyList()
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
