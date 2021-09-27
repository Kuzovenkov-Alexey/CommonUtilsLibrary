package alexey.tools.common.misc

import java.io.Closeable
import java.net.HttpURLConnection
import java.net.URL

inline fun silentTry(block: () -> Unit) = try { block() } catch (_: Throwable) { }

inline fun <T : Closeable, R> T.tryClose(block: T.() -> R): R = try { block() } finally { close() }

fun <T : Enum<T>> String.toEnum(clazz: Class<T>): T = java.lang.Enum.valueOf(clazz, this)

fun <T : Enum<T>> maxId(clazz: Class<T>): Int = clazz.enumConstants.size

fun URL.canRead(): Boolean {
    return try {
        val connection = this.openConnection()
        if (connection is HttpURLConnection) {
            connection.requestMethod = "HEAD"
            connection.responseCode < HttpURLConnection.HTTP_BAD_REQUEST
        } else {
            connection.useCaches = false
            connection.getInputStream().close()
        }
        true
    } catch (_: Throwable) {
        false
    }
}

fun URL.readData(): ByteArray {
    val connection = openConnection()
    val length = connection.contentLength
    if (length < 1) return ByteArray(0)
    val input = connection.getInputStream()
    try {
        val data = ByteArray(length)
        var offset = 0
        var read = input.read(data)
        while (read >= 0) {
            offset += read
            read = input.read(data, offset, length - offset)
        }
        return data
    } finally {
        input.close()
    }
}

fun Any.closeAny() {
    if (this is Closeable) close()
}

