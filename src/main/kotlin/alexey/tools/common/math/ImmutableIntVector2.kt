package alexey.tools.common.math

interface ImmutableIntVector2 {
    fun getX(): Int
    fun getY(): Int
    fun equals(x: Int, y: Int): Boolean
    fun equals(other: ImmutableIntVector2): Boolean
    override fun equals(other: Any?): Boolean
}