package alexey.tools.common.resources

import alexey.tools.common.misc.PathUtils
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipFileResource(file: File) : FileResource(file), Closeable {

    private val zipFile = ZipFile(file)



    constructor(file: String) : this(File(file))



    override fun close() = zipFile.close()

    override fun getResource(relativePath: String): Resource {
        val entry = zipFile.getEntry(PathUtils.normalizePath(relativePath))
        return if (entry == null) Resource.NULL else ZipEntryResource(entry)
    }

    override fun getType(): String = "file-zip"



    private inner class ZipEntryResource(private val zipEntry: ZipEntry) : ResourceBase() {

        override fun getResource(relativePath: String): Resource {
            if (relativePath.isEmpty()) return this
            val toNormalize = StringBuilder(zipFile.name)
            if (toNormalize[toNormalize.length - 1] != '/') toNormalize.append('/')
            toNormalize.append(relativePath)
            return this@ZipFileResource.getResource(toNormalize.toString())
        }

        override fun getInputStream(): InputStream = zipFile.getInputStream(zipEntry)

        override fun getPath(): String = zipFile.name + "!/" + zipEntry.name

        override fun getType(): String = "zip-file-entry"
    }
}