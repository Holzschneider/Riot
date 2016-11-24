package de.dualuse.commons.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ByteArrayInputStream implementation that does not synchronize methods.
 */
public class FastByteArrayInputStream extends ByteArrayInputStream {
    protected byte buf[];
    protected int pos;
    protected int mark = 0;
    protected int count;

    public FastByteArrayInputStream(byte buf[]) {
    	super(buf);
    }

    public FastByteArrayInputStream(byte buf[], int offset, int length) {
    	super(buf,offset,length);
    }

    public int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    public int available() {
        return count - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }

    public void reset() {
        pos = mark;
    }

    public void close() throws IOException {
    }

}
