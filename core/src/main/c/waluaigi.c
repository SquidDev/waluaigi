#include "waluaigi.h"

#include <stdlib.h>

#include "lua/lauxlib.h"
#include "lua/lua.h"
#include "lua/lualib.h"

#define IMPORT(name) __attribute__((import_name(#name), import_module("waluaigi")))
#define EXPORT(name) __attribute__((export_name(#name))) extern

/*******************************************************************************
 * Error handling
 ******************************************************************************/

IMPORT(waluaigi_try)
void _waluaigi_try(lua_State *L, Pfunc f, void *ud);

void waluaigi_try(lua_State *L, Pfunc f, void *ud) { _waluaigi_try(L, f, ud); }

IMPORT(waluaigi_throw)
void _waluaigi_throw();

void waluaigi_throw() { _waluaigi_throw(); }

/** Called from waluaigi_try to actually invoke the function. */
EXPORT(waluaigi_try_invoke)
void waluaigi_try_invoke(lua_State *L, Pfunc f, void *ud) { f(L, ud); }

/*******************************************************************************
 * Timeout code
 ******************************************************************************/

IMPORT(waluaigi_get_timeout)
int _waluaigi_get_timeout();

int waluaigi_get_timeout() {
  int timeout = _waluaigi_get_timeout();
  if (timeout == WA_TIMEOUT_HALT) exit(1);

  return timeout;
}

/*******************************************************************************
 * C -> Java method calls
 ******************************************************************************/

typedef unsigned int waluaigi_handle;
typedef int waluaigi_method;

IMPORT(waluaigi_invoke)
int waluaigi_invoke(lua_State *L, waluaigi_handle handle, waluaigi_method method);

IMPORT(waluaigi_free)
void waluaigi_free(waluaigi_handle handle);

#define WALUAIGI_OBJECT "object"

/** __gc metamethod. Simply pops its argument and invokes waluaigi_free. */
int gc_object(lua_State *L) {
  waluaigi_handle *handle = (waluaigi_handle *)luaL_checkudata(L, 1, WALUAIGI_OBJECT);
  waluaigi_free(*handle);
  return 0;
}

/** Creates the waluaigi metatable. */
int do_waluaigi_setup(lua_State *L) {
  luaL_newmetatable(L, WALUAIGI_OBJECT);

  lua_pushcclosure(L, gc_object, 0);
  lua_setfield(L, -2, "__gc");
  return 1;
}

EXPORT(waluaigi_new_state)
lua_State *waluaigi_new_state() {
  lua_State *L = luaL_newstate();
  if (!L) return NULL;

  luaL_openlibs(L);

  lua_pushcclosure(L, do_waluaigi_setup, 0);
  lua_call(L, 0, 0);

  return L;
}

/** Push a waluaigi object to the stack. */
EXPORT(waluaigi_pushobject) void waluaigi_pushobject(lua_State *L, waluaigi_handle handle) {
  waluaigi_handle *alloc = lua_newuserdatauv(L, sizeof(waluaigi_handle), 1);
  *alloc = handle;
  luaL_setmetatable(L, WALUAIGI_OBJECT);
}

int do_waluaigi_invoke(lua_State *L) {
  waluaigi_handle *handle = (waluaigi_handle *)luaL_checkudata(L, lua_upvalueindex(1), WALUAIGI_OBJECT);
  waluaigi_method method = luaL_checkinteger(L, lua_upvalueindex(2));
  return waluaigi_invoke(L, *handle, method);
}

/** Pop a waluaigi object from the stack and push a function which calls that object's nth method. */
EXPORT(waluaigi_pushfunction) void waluaigi_pushfunction(lua_State *L, waluaigi_method method) {
  lua_pushinteger(L, method);
  lua_pushcclosure(L, do_waluaigi_invoke, 2);
}

/*******************************************************************************
 * Misc VM functionality
 ******************************************************************************/
EXPORT(waluaigi_gc) void waluaigi_gc(lua_State *L) { lua_gc(L, LUA_GCCOLLECT); }
