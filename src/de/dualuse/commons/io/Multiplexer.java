package de.dualuse.commons.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class Multiplexer {
	
	private final DataOutputStream dos;
	
	public Multiplexer(OutputStream os) {
		dos = new DataOutputStream(os);
	}

	private Object sendLock = new Object();
	private void send(Integer index, byte[] data, int offset, int len) throws IOException {
		synchronized(sendLock) {
			
//			System.err.println(index+" muxing "+Arrays.toString(Arrays.copyOfRange(data, offset, offset+len)));
			
			dos.writeInt(index.intValue());
			dos.writeInt(len);
			dos.write(data,offset,len);
		}
	}
	
	public class MultiplexedOutputStream extends OutputStream {
		final Integer index;
		public MultiplexedOutputStream(int i) {
			this.index=i;
		}

		public void flush() throws IOException {
			synchronized(sendLock) {
				dos.flush();
			}
		}
		
		public void write(int b) throws IOException { write(new byte[] {(byte)b}); 	}
		public void write(byte[] b) throws IOException { write(b, 0, b.length); }
		public void write(byte[] b, int off, int len) throws IOException {
			send(index,b,off,len);
		}
	}
	
	
	private HashMap<Integer, MultiplexedOutputStream> outputStreams = new HashMap<Integer, MultiplexedOutputStream>();
	public synchronized OutputStream getOutputStream(int i) {
		MultiplexedOutputStream os = outputStreams.get(i);
		if (os == null) outputStreams.put(i,os = new MultiplexedOutputStream(i));
		return os;
	}

}
