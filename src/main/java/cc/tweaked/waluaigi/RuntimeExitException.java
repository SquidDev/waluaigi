package cc.tweaked.waluaigi;

/**
 * The runtime exited with {@code exit(1)}.
 */
public final class RuntimeExitException extends WasmException {
    private static final long serialVersionUID = -2803250014081420346L;

    private final int code;

    RuntimeExitException(int code) {
        super("Exited with " + code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
