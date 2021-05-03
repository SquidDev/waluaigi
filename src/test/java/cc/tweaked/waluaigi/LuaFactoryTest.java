package cc.tweaked.waluaigi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LuaFactoryTest {
    private int callTracker = 0;

    @Test
    public void testEverything() {
        BasicEnvironment environment = new BasicEnvironment();
        Memory memory = new Memory(8 * 1024 * 1024, 128 * 1024 * 1024);

        Lua lua = LuaFactory.create(environment, memory);
        int intBuffer = lua.malloc(4);
        int stringBuffer = lua.malloc(1024);

        int state = lua.waluaigi_new_state();

        int object = environment.allocate((state2, method) -> {
            assertEquals(state, state2, "The states line up");
            assertEquals(0, method, "Called method #0");

            String message = getString(lua, memory, state2, intBuffer, 1);
            assertEquals("hello", message, "Given the correct argument.");

            callTracker++;
            return 0;
        });

        lua.waluaigi_pushobject(state, object);
        lua.waluaigi_pushfunction(state, 0);
        pushString(memory, stringBuffer, "test_fn");
        lua.lua_setglobal(state, stringBuffer);

        pushString(memory, stringBuffer, "test_fn('hello')");
        assertEquals(0, lua.luaL_loadstring(state, stringBuffer), "Loaded string correctly");

        int res = lua.lua_pcallk(state, 0, 0, 0, 0, 0);
        if (res != 0) {
            String message = getString(lua, memory, state, intBuffer, 1);
            assertEquals(0, res, "Called string correctly (" + message + ")");
        }

        assertEquals(1, callTracker, "Called exactly once.");

        // It's at this point that you wonder if this should have been multiple tests.
        lua.lua_pushnil(state);
        pushString(memory, stringBuffer, "test_fn");
        lua.lua_setglobal(state, stringBuffer);

        lua.waluaigi_gc(state);

        assertTrue(environment.isFree(object), "Object was not GCed");
    }

    private void pushString(Memory memory, int stringBuffer, String string) {
        memory.putString(stringBuffer, string);
        memory.put(stringBuffer + string.length(), (byte) 0);
    }

    private String getString(Lua lua, Memory memory, int state, int intBuffer, int slot) {
        int start = lua.luaL_checklstring(state, slot, intBuffer);
        int len = memory.getInt(intBuffer);
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++) builder.append((char) memory.get(start + i));
        return builder.toString();
    }
}
