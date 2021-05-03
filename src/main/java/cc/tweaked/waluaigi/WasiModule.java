package cc.tweaked.waluaigi;

/**
 * Java bindings for WASI (WebAssembly System Interface).
 *
 * This is an incredibly minimal set of methods. We provide just enough functionality for the Lua VM, and no more.
 */
final class WasiModule {
    public static final String NAME = "wasi_snapshot_preview1";
    public static final WasiModule INSTANCE = new WasiModule();

    private static final int WASI_EBADF = 8;

    private WasiModule() {
    }

    public void proc_exit(int x) {
        throw new RuntimeExitException(x);
    }

    public int clock_time_get(int x, long y, int z) {
        return (int) (System.currentTimeMillis() & Integer.MAX_VALUE);
    }

    public int fd_prestat_get(int fd, int bufPtr) {
        return WASI_EBADF;
    }

    public int fd_seek(int a, long b, int c, int d) {
        throw new MethodNotImplementedException("fd_seek");
    }

    public int fd_write(int a, int b, int c, int d) {
        throw new MethodNotImplementedException("fd_write");
    }

    public int fd_read(int a, int b, int c, int d) {
        throw new MethodNotImplementedException("fd_read");
    }

    public int fd_close(int fd) {
        throw new MethodNotImplementedException("fd_close");
    }

    public int fd_prestat_dir_name(int x, int y, int z) {
        throw new MethodNotImplementedException("fd_prestat_dir_name");
    }

    static final class MethodNotImplementedException extends WasmException {
        private static final long serialVersionUID = 128098287873347999L;

        MethodNotImplementedException(String method) {
            super("Not implemented: " + method);
        }
    }
}
