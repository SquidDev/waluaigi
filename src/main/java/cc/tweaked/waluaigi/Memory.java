package cc.tweaked.waluaigi;

import asmble.compile.jvm.MemoryBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A basic {@link MemoryBuffer} implementation which implements a growable memory buffer.
 */
public class Memory extends MemoryBuffer {
    private final char[] characterBuffer = new char[1024];

    private ByteBuffer buffer;

    private final int initialMemory;
    private final int maxMemory;
    private final boolean isDuplicate;

    public Memory(int initialMemory, int maxMemory) {
        this(ByteBuffer.allocateDirect(initialMemory), initialMemory, maxMemory, false);
    }

    private Memory(ByteBuffer buffer, int initialMemory, int maxMemory, boolean isDuplicate) {
        this.buffer = buffer;
        this.initialMemory = initialMemory;
        this.maxMemory = maxMemory;
        this.isDuplicate = isDuplicate;
    }

    @Override
    public int capacity() {
        return maxMemory;
    }

    @Override
    public int limit() {
        return buffer.limit();
    }

    @Override
    public MemoryBuffer clear() {
        if (isDuplicate) throw new IllegalArgumentException("Cannot clear duplicate buffer");
        buffer.clear();
        return this;
    }

    @Override
    public MemoryBuffer limit(int newLimit) {
        if (isDuplicate) throw new IllegalArgumentException("Cannot limit duplicate buffer");
        if (newLimit <= buffer.capacity()) {
            buffer.limit(newLimit);
            return this;
        }

        if (newLimit > maxMemory) {
            // TODO: Does this ever happen?
            throw new OutOfMemoryException("Out of memory: requested " + newLimit + ", but limited to " + maxMemory);
        }

        int position = buffer.position();
        buffer.position(0);

        ByteBuffer newBuffer = ByteBuffer.allocateDirect(newLimit);
        newBuffer.position(0);
        newBuffer.put(buffer);
        newBuffer.position(position);

        buffer = newBuffer;

        return this;
    }

    @Override
    public MemoryBuffer position(int newPosition) {
        buffer.position(newPosition);
        return this;
    }

    @Override
    public MemoryBuffer order(ByteOrder order) {
        buffer.order(order);
        return this;
    }

    @Override
    public MemoryBuffer duplicate() {
        return new Memory(buffer, initialMemory, maxMemory, true);
    }

    @Override
    public MemoryBuffer put(byte[] arr, int offset, int length) {
        buffer.put(arr, offset, length);
        return this;
    }

    @Override
    public MemoryBuffer put(byte[] arr) {
        buffer.put(arr);
        return this;
    }

    @Override
    public MemoryBuffer put(int index, byte b) {
        buffer.put(index, b);
        return this;
    }

    public MemoryBuffer put(int index, ByteBuffer buffer) {
        int position = this.buffer.position();
        this.buffer.position(index);
        this.buffer.put(buffer);
        this.buffer.position(position);
        return this;
    }

    public MemoryBuffer put(int index, byte[] buffer, int offset, int length) {
        int position = this.buffer.position();
        this.buffer.position(index);
        this.buffer.put(buffer, offset, length);
        this.buffer.position(position);
        return this;
    }

    /**
     * Put an ASCII string into this buffer. This replaces any characters greater than 255 with {@literal '?'}.
     *
     * @param index  The index to insert this string at.
     * @param string The string to put.
     * @return The current buffer.
     */
    public MemoryBuffer putString(int index, String string) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char chr = string.charAt(i);
            buffer.put(index + i, chr < 256 ? (byte) chr : (byte) '?');
        }
        return this;
    }

    @Override
    public MemoryBuffer putInt(int index, int n) {
        buffer.putInt(index, n);
        return this;
    }

    @Override
    public MemoryBuffer putLong(int index, long n) {
        buffer.putLong(index, n);
        return this;
    }

    @Override
    public MemoryBuffer putDouble(int index, double n) {
        buffer.putDouble(index, n);
        return this;
    }

    @Override
    public MemoryBuffer putShort(int index, short n) {
        buffer.putShort(index, n);
        return this;
    }

    @Override
    public MemoryBuffer putFloat(int index, float n) {
        buffer.putFloat(index, n);
        return this;
    }

    @Override
    public byte get(int index) {
        return buffer.get(index);
    }

    @Override
    public int getInt(int index) {
        return buffer.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return buffer.getLong(index);
    }

    @Override
    public short getShort(int index) {
        return buffer.getShort(index);
    }

    @Override
    public float getFloat(int index) {
        return buffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return buffer.getDouble(index);
    }

    @Override
    public MemoryBuffer get(byte[] arr) {
        buffer.get(arr);
        return this;
    }

    public void get(int index, ByteBuffer buffer) {
        int position = this.buffer.position();
        this.buffer.position(index);
        buffer.put(this.buffer);
        this.buffer.position(position);
    }

    public void get(int index, byte[] buffer, int offset, int length) {
        int position = this.buffer.position();
        this.buffer.position(index);
        this.buffer.get(buffer, offset, length);
        this.buffer.position(position);
    }

    /**
     * Get an ASCII string from this buffer.
     *
     * @param index  The index to insert this string at.
     * @param length The length of this string;
     * @return The current buffer.
     */
    public String getString(int index, int length) {
        char[] characters = length <= 1024 ? characterBuffer : new char[length];
        for (int i = 0; i < length; i++) characters[i] = (char) (buffer.get(index + i) & 0xFF);
        return String.valueOf(characters, 0, length);
    }
}
