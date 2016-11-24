package de.dualuse.commons.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

public class ScrambledOutputStream extends FilterOutputStream {
	
	OutputStream out;
	Random rand;
	
	public ScrambledOutputStream(OutputStream out, String name) {
		super(out);
		this.out = out;
		this.rand = new Random(name.hashCode());
	}
	
	@Override public void write(int b) throws IOException {
		byte x = (byte)rand.nextInt(256);
		b = b ^ x;
		out.write(b);
	}
	
	@Override public void write(byte[] b) throws IOException {
		for (int i=0; i<b.length; i++) {
			byte x = (byte)rand.nextInt(256);
			b[i] = (byte) (b[i] ^ x);
		}
		out.write(b);
	}
	
	@Override public void write(byte[] b, int off, int len) throws IOException {
		for (int i=off; i<len; i++) {
			byte x = (byte)rand.nextInt(256);
			b[i] = (byte) (b[i] ^ x);
		}
		out.write(b, off, len);
	}
	
}
