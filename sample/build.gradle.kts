repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

version = "0.1.1"

plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm")
}

application {
    mainClass = "sp.service.sample.AppKt"
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = Version.jvmTarget
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = Version.jvmTarget
}

tasks.getByName<JavaExec>("run") {
    doFirst {
        val os = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
        when {
            os.isMacOsX -> {
                jvmArgs = listOf("-XstartOnFirstThread")
            }
        }
    }
}

dependencies {
    implementation("org.json:json:20231013")
    implementation("com.github.kepocnhh:KotlinExtension.Math:${Version.math}")
    implementation("com.github.kepocnhh:LwjglJoysticks:${Version.Lwjgl.joysticks}")
    implementation(project(":lib"))
    val classifier = Lwjgl.requireNativesName()
    Lwjgl.modules.forEach { name ->
        runtimeOnly(group = Lwjgl.group, name = name, classifier = classifier)
    }
}
