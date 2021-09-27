package alexey.tools.common.math

import java.awt.Point
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*

fun gcd(a: Int, b: Int): Int {
    if (b == 0) return abs(a)
    return gcd(b, a % b)
}

fun mod(a: Float, b: Float): Float = a - floor(a / b) * b

fun roll(winChance: Double): Boolean = ThreadLocalRandom.current().nextDouble(0.0, 1.0) < winChance

fun randomInt(): Int = ThreadLocalRandom.current().nextInt()

fun randomInt(origin: Int, bound: Int): Int = ThreadLocalRandom.current().nextInt(origin, bound)

fun randomDouble(origin: Double, bound: Double): Double = ThreadLocalRandom.current().nextDouble(origin, bound)

fun exponentialRandomDouble(m: Double): Double = ln(1 - ThreadLocalRandom.current().nextDouble()) / (-1.0 / m)

fun randomDouble(): Double = ThreadLocalRandom.current().nextDouble()

fun toGrid(x: Float, y: Float, dx: Int, dy: Int): Point =
    Point((x / dx).roundToInt() * dx, (y / dy).roundToInt() * dy)

inline fun forSquare(x: Int, y: Int, radius: Int, action: (Int, Int) -> Unit) {
    for (i in x - radius .. x + radius) for (j in y - radius .. y + radius) action(i, j)
}

inline fun forSquare(bx: Int, by: Int, ex: Int, ey: Int, action: (Int, Int) -> Unit) {
    for (i in bx .. ex) for (j in by .. ey) action(i, j)
}