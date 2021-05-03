# Waluaigi
The PUC Lua VM, compiled to Java.

## Background
Waluaigi is an experimental Lua implementation for the Java Virtual Machine
(JVM). Unlike other Lua runtimes targeting the JVM, such as [LuaJ] or [Cobalt],
Waluaigi, this uses the sources of the original PUC Lua VM, ensuring an
accuarate experience.

To do this, the C Lua VM is compiled to WebAssembly and then Java, using
[asmble].

## Performance
Unfortunately, this comes at a cost. Waluaigi is about 10x slower than Cobalt
and 100x time slower than PUC Lua. This testing was done using
`scripts/bench.lua`. It's not exactly accurate or representative, but gives a
rough idea.

| Runtime  | Iterations |
|:---------|-----------:|
| PUC Lua  | 5174       |
| Cobalt   | 570        |
| Waluaigi | 50         |

[LuaJ]: https://github.com/luaj/luaj/
[Cobalt]: https://github.com/SquidDev/Cobalt
[asmble]: https://github.com/fluencelabs/asmble
