import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object Lwjgl {
    const val group = "org.lwjgl"
    val modules = setOf(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-freetype",
    )

    fun requireNativesName(): String {
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        return when {
            os.isLinux -> "natives-linux"
            os.isMacOsX -> {
                val architecture = DefaultNativePlatform.getCurrentArchitecture()
                when (architecture.name) {
                    "arm-v8", "aarch64" -> "natives-macos-arm64"
                    else -> error("Operating System ${os.name} with architecture ${architecture.name} is not supported!")
                }
            }
            os.isWindows -> "natives-windows"
            else -> error("Operating System ${os.name} is not supported!")
        }
    }
}
