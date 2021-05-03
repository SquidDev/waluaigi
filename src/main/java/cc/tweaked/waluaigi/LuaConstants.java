package cc.tweaked.waluaigi;

/**
 * Constants defined in {@code lua.h} and {@code luaconf.h}
 */
public final class LuaConstants {
    private LuaConstants() {
    }

    public static final int LUA_VERSION_NUM = 504;

    /**
     * Option for multiple returns in {@link Lua#lua_pcallk} and {@link Lua#lua_callk}.
     */
    public static final int LUA_MULTRET = (-1);


    // thread status
    public static final int LUA_OK = 0;
    public static final int LUA_YIELD = 1;
    public static final int LUA_ERRRUN = 2;
    public static final int LUA_ERRSYNTAX = 3;
    public static final int LUA_ERRMEM = 4;
    public static final int LUA_ERRERR = 5;


    // basic types
    public static final int LUA_TNONE = (-1);

    public static final int LUA_TNIL = 0;
    public static final int LUA_TBOOLEAN = 1;
    public static final int LUA_TLIGHTUSERDATA = 2;
    public static final int LUA_TNUMBER = 3;
    public static final int LUA_TSTRING = 4;
    public static final int LUA_TTABLE = 5;
    public static final int LUA_TFUNCTION = 6;
    public static final int LUA_TUSERDATA = 7;
    public static final int LUA_TTHREAD = 8;

    public static final int LUA_NUMTYPES = 9;


    /**
     * Minimum Lua stack available to a C function
     */
    public static final int LUA_MINSTACK = 20;
}
