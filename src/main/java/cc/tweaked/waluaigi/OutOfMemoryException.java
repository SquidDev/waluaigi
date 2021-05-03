package cc.tweaked.waluaigi;


/**
 * Thrown when the VM attempts to allocate too much memory.
 */
public class OutOfMemoryException extends WasmException {
    private static final long serialVersionUID = -8662460714436626585L;

    public OutOfMemoryException(String message) {
        super(message);
    }
}
