package de.dualuse.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * ByteArrayOutputStream implementation that doesn't synchronize methods
 * and doesn't copy the data on toByteArray().
 */
public class FastByteArrayOutputStream extends ByteArrayOutputStream {
    /**
     * Constructs a stream with buffer capacity count 5K
     */
    public FastByteArrayOutputStream() {
        this(5 * 1024);
    }

    /**
     * Constructs a stream with the given initial count
     */
    public FastByteArrayOutputStream(int initcount) {
        this.count = 0;
        this.buf = new byte[initcount];
    }

    /**
     * Ensures that we have a large enough buffer for the given count.
     */
    private void verifyBuffercount(int sz) {
        if (sz > buf.length) {
            byte[] old = buf;
            buf = new byte[Math.max(sz, 2 * buf.length )];
            System.arraycopy(old, 0, buf, 0, old.length);
            old = null;
        }
    }

    public int getcount() {
        return count;
    }

    /**
     * Returns the byte array containing the written data. Note that this
     * array will almost always be larger than the amount of data actually
     * written.unb
     */
    public byte[] getByteArray() {
        return buf;
    }

    public final void write(byte b[]) {
        verifyBuffercount(count + b.length);
        System.arraycopy(b, 0, buf, count, b.length);
        count += b.length;
    }

    public final void write(byte b[], int off, int len) {
        verifyBuffercount(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public final void write(int b) {
        verifyBuffercount(count + 1);
        buf[count++] = (byte) b;
    }

    public void reset() {
        count = 0;
    }

}
