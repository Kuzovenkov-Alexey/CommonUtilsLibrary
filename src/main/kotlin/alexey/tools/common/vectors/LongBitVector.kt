package alexey.tools.common.vectors

class LongBitVector(var vector: Long = 0): TypeBitVector {
    override fun get(index: Int): Boolean {
        return ((1L shl index) and vector) != 0L
    }

    override fun set(index: Int) {
        vector = vector or (1L shl index)
    }

    override fun clear(index: Int) {
        vector = vector and (1L shl index).inv()
    }

    override fun clear() {
        vector = 0L
    }
}