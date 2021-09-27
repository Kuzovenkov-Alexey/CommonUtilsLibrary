package alexey.tools.common.resources

import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface Resource {
    fun getResource(relativePath: String): Resource = NULL
    fun canRead(): Boolean = try { getInputStream().close(); true } catch (_: Throwable) { false }
    fun getOutputStream(): OutputStream = throw UnsupportedOperationException("getOutputStream")
    fun getInputStream(): InputStream = throw UnsupportedOperationException("getInputStream")
    fun getPath(): String = ""
    fun getType(): String = "undefined"
    fun getFile(): File? = null

    companion object {
        val NULL = object : Resource {
            override fun canRead(): Boolean = false
            override fun getType(): String = "null"
            override fun toString(): String = "null:"
        }
    }
}