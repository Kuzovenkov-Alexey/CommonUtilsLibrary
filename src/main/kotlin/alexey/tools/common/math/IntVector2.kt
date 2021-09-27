package alexey.tools.common.math

class IntVector2(private var x: Int = 0,
                 private var y: Int = 0): ImmutableIntVector2 {

    fun copy(): IntVector2 {
        return IntVector2(x, y)
    }

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun setX(x: Int) {
        this.x = x
    }

    fun setY(y: Int) {
        this.y = y
    }

    override fun equals(x: Int, y: Int): Boolean {
        if (x == this.x && y == this.y) return true
        return false
    }

    fun equals(other: IntVector2): Boolean {
        if (x == other.x && y == other.y) return true
        return false
    }

    override fun equals(other: ImmutableIntVector2): Boolean {
        if (x == other.getX() && y == other.getY()) return true
        return false
    }

    override fun getX(): Int = x

    override fun getY(): Int = y

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IntVector2
        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun hashCode(): Int = 31 * x + y

    override fun toString(): String = "$x, $y"
}