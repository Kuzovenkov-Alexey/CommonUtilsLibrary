package alexey.tools.common.misc

import alexey.tools.common.tasks.ConditionFixedRateTask
import alexey.tools.common.tasks.Task
import alexey.tools.common.tasks.TaskPool
import alexey.tools.common.tasks.TimeFixedRateTask

object TaskUtils {
    val INFINITE = {false}
    val SINGLE = {true}
}

inline fun TaskPool.simple(crossinline action: () -> Unit,
                           crossinline endCondition: () -> Boolean): Task {
    val t = object : Task() {
        override fun update(deltaTime: Float) = action()
        override fun isDone(): Boolean = endCondition()
    }
    add(t)
    return t
}

inline fun TaskPool.single(crossinline action: () -> Unit): Task {
    val t = object : Task() {
        override fun update(deltaTime: Float) = action()
        override fun isDone(): Boolean = true
    }
    add(t)
    return t
}

inline fun TaskPool.infinite(crossinline action: () -> Unit): Task {
    val t = object : Task() {
        override fun update(deltaTime: Float) = action()
        override fun isDone(): Boolean = false
    }
    add(t)
    return t
}

inline fun TaskPool.atFixedRate(delay: Float, remainingDelay: Float, times: Int,
                                crossinline action: () -> Unit): TimeFixedRateTask {
    val t = object : TimeFixedRateTask(times, delay, remainingDelay) {
        override fun action(deltaTime: Float) = action()
    }
    add(t)
    return t
}

inline fun TaskPool.atFixedRate(delay: Float, crossinline action: () -> Unit) =
    atFixedRate(delay, delay, -1, action)

inline fun TaskPool.delay(delay: Float, crossinline action: () -> Unit) =
    atFixedRate(delay, delay, 1, action)

inline fun TaskPool.atFixedRate(delay: Float, duration: Float, crossinline action: () -> Unit) =
    atFixedRate(delay, delay, (duration / delay).toInt(), action)

inline fun TaskPool.atFixedRate(delay: Float, remainingDelay: Float,
                                crossinline endCondition: () -> Boolean,
                                crossinline action: () -> Unit): ConditionFixedRateTask {
    val t = object : ConditionFixedRateTask(delay, remainingDelay) {
        override fun action(deltaTime: Float) = action()
        override fun isDone(): Boolean = endCondition()
    }
    add(t)
    return t
}

inline fun TaskPool.atFixedRate(delay: Float,
                                crossinline endCondition: () -> Boolean,
                                crossinline action: () -> Unit) =
    atFixedRate(delay, delay, endCondition, action)