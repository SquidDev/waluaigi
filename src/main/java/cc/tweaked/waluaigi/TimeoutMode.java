package cc.tweaked.waluaigi;

/**
 * The current timeout mode.
 *
 * This attempts to pause the VM at convenient locations.
 *
 * @see ExecutionEnvironment#getTimeout()
 */
public enum TimeoutMode {
    /**
     * The VM has timed out.
     */
    OK,

    /**
     * The VM should be suspended at its current point.
     */
    PAUSE,

    /**
     * The VM has run for too long and a Lua error raised.
     */
    ERROR,

    /**
     * The VM has continued to run after receiving an error. It should be terminated.
     */
    HALT
}
