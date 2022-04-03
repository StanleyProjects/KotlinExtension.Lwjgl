repositories.mavenCentral()

plugins {
    id("org.jetbrains.kotlin.jvm")
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = Version.jvmTarget
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = Version.jvmTarget
}

dependencies {
    implementation(kotlin("stdlib"))

    val group = "org.lwjgl"
    implementation(platform("$group:lwjgl-bom:${Version.lwjgl}"))
    setOf(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl",
        "lwjgl-stb"
    ).forEach { name ->
        implementation(group = group, name = name)
    }
}
