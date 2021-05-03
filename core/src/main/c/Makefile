PWD=$(CURDIR)

ROOT=$(PWD)/wasi-sdk
OUT=

SYSROOT=$(ROOT)/share/wasi-sysroot

CC=$(ROOT)/bin/clang -std=gnu99 --target=wasm32-wasi
AR=$(ROOT)/bin/ar rcu
RANLIB=$(ROOT)/bin/ranlib

# l_signalT=int (we don't really have the concept of atomics)
# L_tmpname=20 as there's no temporary names in WASI.
CFLAGS=--sysroot=$(SYSROOT) -DLUA_WALUAIGI -Dl_signalT=int -DL_tmpnam=20 -I$(PWD)/stub
LDFLAGS=--sysroot=$(SYSROOT)

ifdef DEBUG
CFLAGS+=-DLUAI_ASSERT -O1
else
CFLAGS+=-flto -O3
LDFLAGS+=-Wl,--lto-O2
endif

LUA_SOURCES=$(shell find lua -type f \( -name '*.c' -or -name '*.h' \) )
WALUAIGI_OBJ=waluaigi.o

COMMA=,

include Makefile.exports

.PHONY: all clean format

all: out.wasm

clean:
	rm -f out.wasm out.wat $(WALUAIGI_OBJ)
	cd lua && $(MAKE) clean

format:
	@clang-format -i *.h *.c

lua/liblua.a: $(LUA_SOURCES)
	cd lua && $(MAKE) CC="$(CC)" AR="$(AR)" RANLIB="$(RANLIB)" SYSCFLAGS="$(CFLAGS)" SYSLDFLAGS="$(LDFLAGS)" liblua.a

out.wat: out.wasm
	wasm2wat $< > $@

out.wasm: lua/liblua.a $(WALUAIGI_OBJ)
	@echo CC -o $@ $^
	@$(ROOT)/bin/clang $(LDFLAGS) -o $@ -nostartfiles -Wl,--no-entry $(addprefix -Wl$(COMMA)--export=, $(EXPORTS)) $^

waluaigi.o: waluaigi.h lua/lapi.h lua/ldo.h lua/lua.h lua/lauxlib.h
