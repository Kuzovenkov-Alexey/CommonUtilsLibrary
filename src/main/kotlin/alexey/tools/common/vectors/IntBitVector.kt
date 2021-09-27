package alexey.tools.common.vectors

class IntBitVector(var vector: Int = 0): TypeBitVector {
    override fun get(index: Int): Boolean {
        return ((1 shl index) and vector) != 0
    }

    override fun set(index: Int) {
        vector = vector or (1 shl index)
    }

    override fun clear(index: Int) {
        vector = vector and (1 shl index).inv()
    }

    override fun clear() {
        vector = 0
    }
}