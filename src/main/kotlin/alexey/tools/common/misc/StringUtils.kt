package alexey.tools.common.misc

fun String.deleteChar(char: Char): String = deleteChar(indexOf(char))

fun String.deleteLastChar(char: Char): String = deleteChar(lastIndexOf(char))

private fun String.deleteChar(index: Int): String {
    if (index == -1) return this
    val sb = StringBuilder(length - 1)
    sb.append(this, 0, index)
    sb.append(this, index + 1, length)
    return sb.toString()
}

fun String.regionMatches(thisOffset: Int, other: String): Boolean =
    this.regionMatches(thisOffset, other, 0, other.length)

fun String.toLowercaseWords() = buildString {
    for (c in this@toLowercaseWords) {
        if (c in 'A'..'Z') {
            if (isNotEmpty()) append('_')
            append(c.lowercaseChar())
        } else append(c)
    }
}

fun StringBuilder.appendLowercaseWords(value: String, removePostfix: Int) {
    val end = value.length - removePostfix
    var i = 0
    while (i < end) {
        val c = value[i++]
        if (c in 'A'..'Z') {
            if (i != 1) append('_')
            append(c.lowercaseChar())
        } else append(c)
    }
}

val EMPTY_STRING_ARRAY = emptyArray<String>()