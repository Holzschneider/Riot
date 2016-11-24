package de.dualuse.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PipedBufferedOutputStream extends OutputStream {
	PipedBufferedInputStream connected;
	
	public PipedBufferedOutputStream() { }
	public PipedBufferedOutputStream(PipedBufferedInputStream connector) {
		this.connected = connector;
	}
	
	public void write(InputStream s) throws IOException {
		connected.drain(s);
	}

	public void write(InputStream s, int len) throws IOException {
		connected.drain(s,len);
	}
	
	@Deprecated
	@Override
	public void write(int b) throws IOException {
		connected.append(new byte[] { (byte)(b&0xFF) },0,1);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		connected.append(bytes, 0, bytes.length);
	}

	@Override
	public void write(byte[] bytes, int offset, int len) throws IOException {
		connected.append(bytes, offset, len);
	}

	@Override public void flush() throws IOException { }
	@Override public void close() throws IOException {
		connected = null;
	}
	
	public PipedBufferedInputStream getConnectedInputStream() {
		return connected;
	}


}
