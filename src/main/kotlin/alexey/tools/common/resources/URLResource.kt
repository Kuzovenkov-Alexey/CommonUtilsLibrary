package alexey.tools.common.resources

import alexey.tools.common.misc.canRead
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class URLResource(val url: URL): ResourceBase() {
    constructor(url: String) : this(URL(url))
    override fun getResource(relativePath: String): Resource = URLResource(URL(url, relativePath))
    override fun getType(): String = "url"
    override fun getPath(): String = url.toString()
    override fun getOutputStream(): OutputStream = url.openConnection().getOutputStream()
    override fun getInputStream(): InputStream = url.openStream()
    override fun canRead(): Boolean = url.canRead()
}