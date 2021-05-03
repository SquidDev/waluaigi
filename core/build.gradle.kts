plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

val asmbleBuild by configurations.creating

val output by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
    extendsFrom(configurations["implementation"], configurations["runtimeOnly"])
}

dependencies {
    asmbleBuild("com.github.cretz.asmble:asmble-compiler:0.4.11-fl")

    "implementation"("com.github.cretz.asmble:asmble-compiler:0.4.11-fl")
    "implementation"("com.github.cretz.asmble:asmble-annotations:0.4.11-fl")
}


val compileWasm by tasks.registering(Exec::class) {
    val sources = file("src/main/c")

    description = "Compile C to WASM"
    inputs.files(fileTree(sources))
    outputs.file(file("src/main/c/out.wasm"))

    workingDir(sources)
    commandLine(listOf("make", "out.wasm"))
}

val compileAsmble by tasks.registering(JavaExec::class) {
    val className = "cc.tweaked.waluaigi.Lua"
    val input = file("src/main/c/out.wasm")
    val output = buildDir.resolve("compileAsmble/${className.replace('.', '/')}.class")

    description = "Compile WASM to Java bytecode"
    dependsOn(compileWasm)
    inputs.file(input)
    outputs.file(output)

    main = "asmble.cli.MainKt"
    classpath = asmbleBuild
    args("compile", input.absolutePath, className, "-out", output.absolutePath, "-log", "info")

    doFirst {
        output.parentFile.mkdirs()
    }
}

val asmbleJar by tasks.registering(Jar::class) {
    dependsOn(compileAsmble)
    from(buildDir.resolve("compileAsmble"))

    archiveBaseName.set("waluigi-core")
}

artifacts {
    add("output", asmbleJar)
}
