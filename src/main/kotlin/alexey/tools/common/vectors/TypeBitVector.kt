package alexey.tools.common.vectors

interface TypeBitVector {
    fun get(index: Int): Boolean
    fun set(index: Int)
    fun clear(index: Int)
    fun clear()
}