# C -> Java bytecode toolchain

This is the core sources and toolchain for compiling the Lua VM to Java
bytecode.

## Dependencies
 - Make
 - The [wasi-sdk], installed at `src/main/c/wasi-sdk`

## Build process
 - `src/main/c/Makefile` compiles Lua and our sources to WASM.
 - Gradle invokes this makefile, and then uses [asmble] to compile this to Java.
 - We then expose this as a jar, which is then depended on by the actual
   waluaigi library.

[wasi-sdk]: https://github.com/WebAssembly/wasi-sdk/
[asmble]: https://github.com/fluencelabs/asmble
