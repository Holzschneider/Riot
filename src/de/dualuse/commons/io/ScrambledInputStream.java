package de.dualuse.commons.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ScrambledInputStream extends FilterInputStream {

	InputStream in;
	Random rand;
	
	protected ScrambledInputStream(InputStream in, String name) {
		super(in);
		this.in = in;
		this.rand = new Random(name.hashCode());
	}
	
	@Override public int read() throws IOException {
		int result = in.read();
		if (result == -1) return result;
		byte x = (byte)rand.nextInt(256);
		return result ^ x;
	}
	
	@Override public int read(byte[] b) throws IOException {
		int result = in.read(b);
		for (int i=0; i<result; i++) {
			byte x = (byte)rand.nextInt(256);
			b[i] = (byte)(b[i] ^ x);
		}
		return result;
	}
	
	@Override public int read(byte[] b, int off, int len) throws IOException {
		int result = in.read(b,  off,  len);
		for (int i=off; i<result; i++) {
			byte x = (byte)rand.nextInt(256);
			b[i] = (byte)(b[i] ^ x);
		}
		return result;
	}
	
}
