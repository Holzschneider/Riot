package de.dualuse.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class TappedInputStream extends InputStream {
	final InputStream is;
	final OutputStream os;
	
	public TappedInputStream(InputStream recorded, OutputStream recorder) {
		this.is = recorded;
		this.os = recorder;
	}
	
	@Override
	public void close() throws IOException {
		is.close();
		os.close();
	}
	
	@Override
	public int read() throws IOException {
		int b = is.read();
		os.write(b);
		return b;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int bytesRead = is.read(b);
		if (bytesRead>0)
			os.write(b, 0, bytesRead);
		return bytesRead;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int bytesRead = is.read(b, off, len);
		if (bytesRead>0)
			os.write(b, off, bytesRead);
		return bytesRead;
	}
	
}