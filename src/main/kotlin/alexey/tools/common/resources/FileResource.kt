package alexey.tools.common.resources

import java.io.File
import java.io.InputStream
import java.io.OutputStream

open class FileResource(private val file: File) : ResourceBase() {
    constructor(file: String) : this(File(file))
    override fun getResource(relativePath: String): Resource = FileResource(File(file, relativePath))
    override fun getPath(): String = file.invariantSeparatorsPath
    override fun getType(): String = "file"
    override fun canRead(): Boolean = file.canRead()
    override fun getOutputStream(): OutputStream = file.run { parentFile.mkdirs(); outputStream() }
    override fun getInputStream(): InputStream = file.inputStream()
    override fun getFile(): File = file
}