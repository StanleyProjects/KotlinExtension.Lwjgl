package sp.service.sample.util

internal fun <T : Any> Iterable<T>.noneOrSingleOrError(predicate: (T) -> Boolean): T? {
    val filtered = filter(predicate)
    if (filtered.isEmpty()) return null
    if (filtered.size == 1) return filtered[0]
    TODO()
}
