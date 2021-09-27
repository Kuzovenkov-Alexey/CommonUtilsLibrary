package alexey.tools.common.resources

class ResourceFactory {

    private val converters = HashMap<String, (String) -> Resource>()
    var separator = ':'

    fun create(spec: String): Resource {
        val i = spec.indexOf(separator)
        if (i == -1) return converters[spec]?.invoke("")
            ?: throw NoSuchElementException("Converter not found ($spec)!")
        val type = spec.substring(0, i)
        return converters[type]?.invoke(spec.substring(i + 1))
            ?: throw NoSuchElementException("Converter not found ($type)!")
    }

    fun setConverter(type: String, converter: (String) -> Resource) {
        converters[type] = converter
    }

    companion object {
        fun newDefaultInstance(): ResourceFactory {
            val result = ResourceFactory()
            result.setConverter("file") { FileResource(it) }
            result.setConverter("url") { URLResource(it) }
            result.setConverter("file-zip") { ZipFileResource(it) }
            result.setConverter("loader") { ClassLoaderData() }
            return result
        }
    }
}