package cc.tweaked.waluaigi;

import asmble.annotation.WasmImport;
import asmble.compile.jvm.MemoryBuffer;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class LuaFactory {
    private LuaFactory() {
    }

    /**
     * Construct a new WASM instance with {@code liblua.a} loaded.
     *
     * @param environment The execution environment for this instance. Holds a pool of Java objects which can be
     *                    interacted with from Lua.
     * @param memory      Memory allocated for this instance.
     * @return The constructed instance.
     */
    public static Lua create(ExecutionEnvironment environment, MemoryBuffer memory) {
        WaluaigiModule waluaigi = new WaluaigiModule(environment);

        Map<String, Object> modules = new HashMap<>();
        modules.put(WasiModule.NAME, WasiModule.INSTANCE);
        modules.put("waluaigi", waluaigi);

        Lua lua = construct(Lua.class, memory, modules);
        waluaigi.setLua(lua);
        return lua;
    }

    private static <T> T construct(Class<T> klass, MemoryBuffer buffer, Map<String, ?> modules) {
        try {
            return constructImpl(klass, buffer, modules);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T constructImpl(Class<T> klass, MemoryBuffer buffer, Map<String, ?> values) throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) Arrays.stream(klass.getConstructors())
            .filter(x -> x.getParameterCount() > 0 && x.getParameterTypes()[0] == MemoryBuffer.class)
            .findFirst().orElseThrow(NullPointerException::new);

        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        args[0] = buffer;

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            WasmImport wasmImport = parameter.getAnnotation(WasmImport.class);

            if (wasmImport == null) {
                throw new IllegalArgumentException("All arguments must be annotated with @WasmImport");
            }

            Object module = values.get(wasmImport.module());
            if (module == null) throw new NullPointerException("Missing module " + module);

            Method method = Arrays.stream(module.getClass().getMethods()).filter(x -> x.getName().equals(wasmImport.field()))
                .findFirst().orElseThrow(() -> new NullPointerException("Missing method " + wasmImport.field() + wasmImport.desc()));

            args[i] = lookup.unreflect(method).bindTo(module);
        }

        return constructor.newInstance(args);
    }
}
