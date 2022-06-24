repositories.mavenCentral()

plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm")
}

application {
    mainClass.set("sp.service.sample.AppKt")
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
    implementation(files("libs/KotlinExtension.Math-${Version.math}.jar"))
    implementation(project(":lib"))
    val group = LwjglUtil.group
    implementation(platform("$group:lwjgl-bom:${Version.lwjgl}"))
    val classifier = LwjglUtil.requireNativesName()
    LwjglUtil.modules.forEach { name ->
        runtimeOnly(group = group, name = name, classifier = classifier)
    }
}
