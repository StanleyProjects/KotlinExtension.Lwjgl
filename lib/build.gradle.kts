import sp.gx.core.Badge
import sp.gx.core.GitHub
import sp.gx.core.Markdown
import sp.gx.core.Maven
import sp.gx.core.camelCase
import sp.gx.core.check
import sp.gx.core.colonCase
import sp.gx.core.kebabCase
import sp.gx.core.resolve
import java.util.Locale

version = "0.1.0"

val maven = Maven.Artifact(
    group = "com.github.kepocnhh",
    id = rootProject.name,
)

val gh = GitHub.Repository(
    owner = "StanleyProjects",
    name = rootProject.name,
)

repositories {
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

plugins {
    id("org.jetbrains.kotlin.jvm")
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = Version.jvmTarget
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = Version.jvmTarget
        freeCompilerArgs = freeCompilerArgs + setOf("-module-name", "com.github.kepocnhh:KotlinExtension.Lwjgl")
    }
}

dependencies {
    implementation("com.github.kepocnhh:KotlinExtension.Math:${Version.math}")
    val group = LwjglUtil.group
    implementation(platform("$group:lwjgl-bom:${Version.lwjgl}"))
    LwjglUtil.modules.forEach { name ->
        implementation(group = group, name = name)
    }
}

"snapshot".also { variant ->
    val version = kebabCase(version.toString(), variant.uppercase(Locale.US))
    // todo
    task(camelCase("check", variant, "Readme")) {
        doLast {
            val badge = Markdown.image(
                text = "version",
                url = Badge.url(
                    label = "version",
                    message = version,
                    color = "2962ff",
                ),
            )
            val expected = setOf(
                badge,
                Markdown.link("Maven", Maven.Snapshot.url(maven, version)),
                Markdown.link("Documentation", gh.pages().resolve("doc").resolve(version)),
                "implementation(\"${colonCase(maven.group, maven.id, version)}\")",
            )
            val report = layout.buildDirectory.get()
                .dir("reports/analysis/readme")
                .file("index.html")
                .asFile
            rootDir.resolve("README.md").check(
                expected = expected,
                report = report,
            )
        }
    }
}
