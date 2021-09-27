package alexey.tools.common.resources

class ClassLoaderData(val classLoader: ClassLoader = ClassLoaderData::class.java.classLoader) : Module, ResourceBase() {
    override fun getObject(name: String): Any = getType(name).getDeclaredConstructor().newInstance()
    override fun getType(name: String): Class<*> = classLoader.loadClass(name)

    override fun getResource(relativePath: String): Resource {
        return URLResource(classLoader.getResource(relativePath) ?: return Resource.NULL)
    }
    override fun canRead(): Boolean = false
    override fun getPath(): String = classLoader.name
    override fun getType(): String = "loader"
}