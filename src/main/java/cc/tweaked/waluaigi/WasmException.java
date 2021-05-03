package cc.tweaked.waluaigi;

/**
 * An exception thrown within the WASM runtime.
 *
 * @see WasmException
 * @see RuntimeExitException
 */
public class WasmException extends RuntimeException {
    private static final long serialVersionUID = 4812044856850120366L;

    public WasmException() {
    }

    public WasmException(String message) {
        super(message);
    }
}
