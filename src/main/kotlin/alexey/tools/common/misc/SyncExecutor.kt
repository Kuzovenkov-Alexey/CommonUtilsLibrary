package alexey.tools.common.misc

import alexey.tools.common.collections.ObjectCollection
import java.util.concurrent.Executor

class SyncExecutor: Executor {

    private var forRead = ObjectCollection<Runnable>()
    private var forWrite = ObjectCollection<Runnable>()

    override fun execute(command: Runnable) {
        synchronized(forWrite) { forWrite.add(command) }
    }

    fun execute() {
        forRead.clear()
        synchronized(forWrite) {
            forRead = forWrite.also { forWrite = forRead }
        }
        forRead.forEach(Runnable::run)
    }
}