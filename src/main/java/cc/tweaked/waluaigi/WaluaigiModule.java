package cc.tweaked.waluaigi;

/**
 * The default modules. These bind functions defined in {@code waluaigi.h}.
 */
final class WaluaigiModule {
    private final ExecutionEnvironment environment;
    private Lua lua;

    WaluaigiModule(ExecutionEnvironment environment) {
        this.environment = environment;
    }

    void setLua(Lua lua) {
        this.lua = lua;
    }

    public void waluaigi_try(int L, int f, int ud) {
        try {
            lua.waluaigi_try_invoke(L, f, ud);
        } catch (InternalException ignored) {
        }
    }

    public void waluaigi_throw() {
        throw InternalException.INSTANCE;
    }

    public int waluaigi_get_timeout() {
        return environment.getTimeout().ordinal();
    }

    public int waluaigi_invoke(int luaState, int object, int method) {
        return environment.invoke(luaState, object, method);
    }

    public void waluaigi_free(int object) {
        environment.free(object);
    }

    static final class InternalException extends RuntimeException {
        private static final long serialVersionUID = -1578756486808160716L;

        static final InternalException INSTANCE = new InternalException();

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
