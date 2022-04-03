import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

object LwjglUtil {
    const val group = "org.lwjgl"
    val modules = setOf(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-stb"
    )

    fun requireNativesName(): String {
        val os = DefaultNativePlatform.getCurrentOperatingSystem()
        return when {
            os.isLinux -> "natives-linux"
            else -> error("Operating System ${os.name} not supported!")
        }
    }
}
