package alexey.tools.common.misc

import alexey.tools.common.resources.Resource
import java.io.InputStream
import java.io.Reader

fun Resource.readText(): String = getInputStream().reader().tryClose(Reader::readText)

fun Resource.readBytes(): ByteArray = getInputStream().tryClose(InputStream::readBytes)

inline fun Resource.tryResource(path: String, action: (Resource) -> Unit) =
    silentTry { getResource(path).run { if (canRead()) action(this) } }

inline fun Resource.tryInputStream(path: String, action: (InputStream) -> Unit) =
    withResource(path) { action(getInputStream()) }

inline fun Resource.withResource(path: String, action: Resource.() -> Unit) =
    silentTry { getResource(path).run { if (this !== Resource.NULL) action() } }