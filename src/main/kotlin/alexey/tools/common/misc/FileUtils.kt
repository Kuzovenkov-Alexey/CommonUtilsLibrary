package alexey.tools.common.misc

import alexey.tools.common.collections.ObjectCollection
import java.io.File

private fun File.listAllFiles(result: ObjectCollection<File>): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result) else
                result.add(it)
        }
    return result
}

private fun File.listAllFiles(result: ObjectCollection<File>, extensions: Array<out String>): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result, extensions) else
                for (extension in extensions)
                    if (it.path.endsWith(extension, true)) {
                        result.add(it)
                        break
                    }
        }
    return result
}

private fun File.listAllFiles(result: ObjectCollection<File>, extension: String): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (it.isDirectory)
                it.listAllFiles(result, extension) else
                if (it.path.endsWith(extension, true))
                    result.add(it)
        }
    return result
}

private fun File.listJustFiles(result: ObjectCollection<File>, extension: String): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (!it.isDirectory && it.path.endsWith(extension, true))
                result.add(it)
        }
    return result
}

private fun File.listJustFiles(result: ObjectCollection<File>, extensions: Array<out String>): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (!it.isDirectory)
                for (extension in extensions)
                    if (it.path.endsWith(extension, true)) {
                        result.add(it)
                        break
                    }
        }
    return result
}

private fun File.listJustFiles(result: ObjectCollection<File>): ObjectCollection<File> {
    if (this.isDirectory)
        this.listFiles()?.forEach {
            if (!it.isDirectory) result.add(it)
        }
    return result
}

fun listJustFiles(directory: String): Collection<File> = File(directory).listJustFiles()

fun listJustFiles(directory: String, extension: String): Collection<File> = File(directory).listJustFiles(extension)

fun listJustFiles(directory: String, vararg extensions: String): Collection<File> = File(directory).listJustFiles(*extensions)

fun File.listJustFiles(): Collection<File> = this.listJustFiles(ObjectCollection())

fun File.listJustFiles(extension: String): Collection<File> = this.listJustFiles(ObjectCollection(), extension)

fun File.listJustFiles(vararg extensions: String): Collection<File> = this.listJustFiles(ObjectCollection(), extensions)

fun listAllFiles(directory: String): Collection<File> = File(directory).listAllFiles()

fun listAllFiles(directory: String, extension: String): Collection<File> = File(directory).listAllFiles(extension)

fun listAllFiles(directory: String, vararg extensions: String): Collection<File> = File(directory).listAllFiles(*extensions)

fun File.listAllFiles(): Collection<File> = this.listAllFiles(ObjectCollection())

fun File.listAllFiles(extension: String): Collection<File> = this.listAllFiles(ObjectCollection(), extension)

fun File.listAllFiles(vararg extensions: String): Collection<File> = this.listAllFiles(ObjectCollection(), extensions)