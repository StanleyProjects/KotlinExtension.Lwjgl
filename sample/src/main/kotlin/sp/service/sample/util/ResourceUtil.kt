package sp.service.sample.util

import java.io.InputStream

object ResourceUtil {
    private fun getClassLoader(): ClassLoader {
        return ResourceUtil::class.java.classLoader
    }

    fun getResourceAsStreamOrNull(filePath: String): InputStream? {
        return getClassLoader().getResourceAsStream(filePath)
    }

    fun requireResourceAsStream(filePath: String): InputStream {
        return getResourceAsStreamOrNull(filePath) ?: error("Resource by path $filePath does not exist!")
    }
}
