package alexey.tools.common.misc

import alexey.tools.common.collections.ObjectCollection

// Synchronized object iterator
class ObjectCollector <T> {

    private var bufferOne = ObjectCollection<T>()
    private var bufferTwo = ObjectCollection<T>()

    fun add(o: T) { // ANY thread
        synchronized(bufferOne) {
            bufferOne.add(o)
        }
    }

    fun addAll(o: ObjectCollection<T>) { // ANY thread
        synchronized(bufferOne) {
            bufferOne.addAll(o)
        }
    }

    fun swap() { // MAIN thread
        val t = bufferOne
        synchronized(bufferOne) {
            bufferOne = bufferTwo
        }
        bufferTwo = t
    }

    fun clear() { // MAIN thread
        bufferTwo.clear()
    }

    inline fun collect(action: (T) -> Unit) { // MAIN thread
        swap()
        getBuffer().forEach(action)
        clear()
    }

    fun collect(destination: ObjectCollection<T>) { // MAIN thread
        swap()
        copyTo(destination)
        clear()
    }

    fun copyTo(destination: ObjectCollection<T>) { // MAIN thread
        destination.addAll(bufferTwo)
    }

    fun getBuffer(): Iterable<T> { // MAIN thread
        return bufferTwo
    }
}