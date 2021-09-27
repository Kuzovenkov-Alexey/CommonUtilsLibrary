package alexey.tools.common.resources

interface Module {
    fun getObject(name: String): Any = throw UnsupportedOperationException("getObject")
    fun getType(name: String): Class<*> = throw UnsupportedOperationException("getObject")

    companion object {
        val NULL = object : Module {}
    }
}