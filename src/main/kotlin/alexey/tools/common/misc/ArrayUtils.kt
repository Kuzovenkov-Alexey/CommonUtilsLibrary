package alexey.tools.common.misc

fun <T> Array<T>.moveOnTop(index: Int) {
    if (index < 1) return
    val e = this[index]
    var i = index - 1
    while (i > -1) {
        this[i + 1] = this[i]
        i--
    }
    this[0] = e
}

operator fun <T> Array<T>.minus(element: T): Array<T> = ArrayUtils.minus(this, element)

fun <T> Array<T>.containsReference(element: T): Boolean {
    for (i in indices) if (this[i] === element) return true
    return false
}