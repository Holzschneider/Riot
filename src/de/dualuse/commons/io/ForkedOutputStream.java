package de.dualuse.commons.io;

import java.io.IOException;
import java.io.OutputStream;

public class ForkedOutputStream extends OutputStream {
	
	final OutputStream[] streams;
	
	public ForkedOutputStream(OutputStream... wrapped) {
		this.streams = wrapped.clone();
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream os: streams)
			os.write(b);
	}


	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream os: streams)
			os.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream os: streams)
			os.write(b, off, len);
		
	}
	
	@Override
	public void close() throws IOException {
		for (OutputStream os: streams)
			os.close();
	}
	
	@Override
	public void flush() throws IOException {
		for (OutputStream os: streams)
			os.flush();
	}

	
}
