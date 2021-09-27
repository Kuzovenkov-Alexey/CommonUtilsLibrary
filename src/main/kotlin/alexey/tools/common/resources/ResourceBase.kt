package alexey.tools.common.resources

abstract class ResourceBase : Resource {

    override fun toString(): String = getType() + ':' + getPath()

    override fun equals(other: Any?): Boolean {
        if (other === null) return false
        if (this === other) return true
        if (javaClass !== other.javaClass) return false
        return (other as Resource).getPath() == getPath()
    }

    override fun hashCode(): Int = getType().hashCode() * 31 + getPath().hashCode()
}