buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.0-beta1")
    }
}

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://squiddev.cc/maven")
    }
}

group = "cc.tweaked.waluaigi"
version = "1.0-SNAPSHOT"

java {
    // We need to produce jars which run on Forge, so force Java 8.
    // Also makes our Proguard configuration easier.
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    withSourcesJar()
}

dependencies {
    implementation(project(mapOf("path" to ":core", "configuration" to "output")))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("min")
}

val shadowJar = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    relocate("asmble.compile.jvm", "cc.tweaked.waluaigi.asmble")
    relocate("asmble.annotation", "cc.tweaked.waluaigi.asmble")
    relocate("kotlin", "cc.tweaked.waluaigi.internal.kotlin")

    minimize()

    exclude("module-info.class")
    exclude("**/*.kotlin_metadata")
    exclude("**/*.kotlin_builtins")

    exclude("META-INF/**")
}

val proguardJar by tasks.registering(proguard.gradle.ProGuardTask::class) {
    dependsOn(shadowJar)

    val libPath = javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    libraryjars(libPath.get().metadata.installationPath.file("jre/lib/rt.jar"))

    injars(mapOf("filter" to "!META-INF/**"), shadowJar.get().archiveFile.get())
    outjars(buildDir.resolve("libs/waluigi.jar"))

    dontobfuscate()
    dontoptimize()
    keepattributes()
    keepparameternames()

    keep("class cc.tweaked.waluaigi.* { *; }")

    dontwarn("java.lang.invoke.MethodHandle")
}

val proguardArtifact = artifacts.add("archives", proguardJar) {
    classifier = null
    type = "jar"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
    }
}

publishing {
    publications {
        create<MavenPublication>("waluaigi") {
            artifact(proguardArtifact)
            artifact(tasks.named("sourcesJar"))
        }
    }

    repositories {
        if (project.hasProperty("mavenUser")) {
            maven {
                name = "SquidDev"
                url = uri("https://squiddev.cc/maven")
                credentials {
                    username = project.property("mavenUser") as String
                    password = project.property("mavenPass") as String
                }
            }
        }
    }
}
