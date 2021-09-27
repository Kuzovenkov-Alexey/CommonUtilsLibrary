package alexey.tools.common.misc

import alexey.tools.common.collections.*

inline fun ImmutableIntCollection.forEach(action: (Int) -> Unit) {
    var i = 0
    val s = size()
    while (i < s) action(unsafeGet(i++))
}

inline fun <T> ImmutableObjectCollection<T>.forEach(action: (T) -> Unit) {
    var i = 0
    val s = size()
    while (i < s) action(unsafeGet(i++))
}

inline fun IntCollection.forEach(action: (Int) -> Unit) {
    var i = 0
    val s = size()
    val d = data()
    while (i < s) action(d[i++])
}

inline fun <T> ObjectStorage<T>.getOrExtendSet(index: Int, defaultValue: () -> T): T {
    var e: T
    if (index < size) {
        e = unsafeGet(index)
        if (e == null) { e = defaultValue(); unsafeSet(index, e) }
    } else {
        e = defaultValue()
        unsafeExtendSetSize(index + 1)
        unsafeSet(index, e)
    }
    return e
}

inline fun <T> MutableIterable<T>.removeFirst(predicate: (T) -> Boolean): T? {
    val i = iterator()
    while (i.hasNext()) {
        val result = i.next()
        if (predicate(result)) { i.remove(); return result }
    }
    return null
}

inline fun <T> CompactObjectStorage<T>.getOrExtendSet(index: Int, default: () -> T): T {
    var e: T
    if (index < size) {
        e = unsafeGet(index)
        if (e == null) { e = default(); set(index, e) }
    } else {
        e = default()
        unsafeSetCapacity(index + 1)
        set(index, e)
    }
    return e
}