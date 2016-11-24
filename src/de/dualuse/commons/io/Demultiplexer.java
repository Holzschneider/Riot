package de.dualuse.commons.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Demultiplexer {

	private final DataInputStream dis;
	
	public Demultiplexer(InputStream os) {
		dis = new DataInputStream(os);
	}
	
	
	private HashMap<Integer,PipedBufferedInputStream> inboundCache = new HashMap<Integer,PipedBufferedInputStream>();
	private PipedBufferedInputStream cacheForIndex(Integer index) {
		synchronized(lock) {
			PipedBufferedInputStream myCache = inboundCache.get(index);
			if (myCache==null) inboundCache.put(index, myCache = new PipedBufferedInputStream());
			return myCache;
		}
	}

	private Object lock = this;//new Object();
	private int waiters = 0;
	
	private int receive(Integer index, byte[] data, int offset, int len) throws IOException {

		boolean watcher = false;
		PipedBufferedInputStream myCache = cacheForIndex(index);
		for (int readIndex = 0, bytesDrained = 0, bytesExpected = 0, bytesRead = 0;;) try {
			synchronized (lock) {
				if (watcher) { 
					for (	bytesDrained=0, bytesRead=0, bytesExpected=dis.readInt(); 
							bytesDrained<bytesExpected; 
							bytesDrained+=bytesRead	)
						bytesRead = cacheForIndex(readIndex).drain(dis,bytesExpected-bytesDrained);
					
					waiters = 0;
					lock.notifyAll();
				}
								
				if (myCache.available()>0) 
					return myCache.read(data, offset, len);
				
				watcher = waiters++==0;
				if (!watcher) 
					lock.wait();
			}
		
			if (watcher) 
				readIndex = dis.readInt();
		} catch (InterruptedException ie) {
			return 0;
		} catch (EOFException ee) {
			synchronized (lock) {
				waiters = 0;
				lock.notifyAll();
			}
			return -1;
		} catch (IOException ioe) {
			synchronized (lock) {
				waiters = 0;
				lock.notifyAll();
			}
			throw ioe;
		}
		
	}
	
	public class DemultiplexedInputStream extends InputStream {
		final Integer index;
		public DemultiplexedInputStream(int i) {
			index = i;
		}
		
		public int read() throws IOException { byte[] b = new byte[1]; int bytesRead = this.read(b,0,1); return bytesRead==-1?-1:(b[0]&0x000000FF); }
		public int read(byte[] b) throws IOException { return this.read(b,0,b.length); }
		public int read(byte[] b, int off, int len) throws IOException {
			return receive(index, b, off, len);
		}
		
		public int available() throws IOException {
			PipedBufferedInputStream cache = cacheForIndex(index);
			synchronized(cache) {
				return cache.available();
			}
		}
	}
	
	private HashMap<Integer, DemultiplexedInputStream> inputStreams = new HashMap<Integer, DemultiplexedInputStream>();
	public InputStream getInputStream(int i) {
		synchronized(lock) {
			DemultiplexedInputStream is = inputStreams.get(i);
			if (is == null) inputStreams.put(i,is = new DemultiplexedInputStream(i));
			return is;
		}
	}
	
}
