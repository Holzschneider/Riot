package de.dualuse.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;

public class PipedBufferedInputStream extends InputStream {
	private static final int DEFAULT_FRAGMENT_SIZE = 8192*2*2*2; //64k
	private final int FRAGMENT_SIZE;
	
	private ArrayDeque<byte[]> recycled = new ArrayDeque<byte[]>();
	private ArrayDeque<byte[]> data = new ArrayDeque<byte[]>();
	private int head = 0, tail = 0;
	
	public int available() {
		return head-tail;
	}
	
	public PipedBufferedInputStream() {
		this(DEFAULT_FRAGMENT_SIZE);
	}
	
	public PipedBufferedInputStream(int fragmentSize) {
		this.FRAGMENT_SIZE = fragmentSize;
		data.add(new byte[FRAGMENT_SIZE]);
	}
	
	public PipedBufferedInputStream(PipedBufferedOutputStream pos) {
		this();
		pos.connected = this;
	}

	public PipedBufferedInputStream(PipedBufferedOutputStream pos, int fragmentSize) {
		this(fragmentSize);
		pos.connected = this;
	}

	
	private boolean closed = false;
	
	public synchronized void close() {
		closed = true;
		notifyAll();
	}
	
	public boolean isClosed() {
		return closed;
	};
	
	private static int drain(InputStream is, byte[] buf, int offset, int len) throws IOException {
		int counter = 0;
		for (int bytesRead=0;len>0;bytesRead=is.read(buf,offset,len),len-=bytesRead,offset+=bytesRead, counter+=bytesRead)
			if (bytesRead==-1)
				return counter-=bytesRead;
		
		return counter;
	}
	
	public synchronized int drain(InputStream s) throws IOException { return this.drain(s, Integer.MAX_VALUE); }
	public synchronized int drain(InputStream s, int len) throws IOException {
		int counter = 0;
		int occupied = head % FRAGMENT_SIZE; //amount of bytes already resident in the current storage fragment
		int remaining = FRAGMENT_SIZE-occupied; //amount of bytes that can be filled into the storage fragment
		
		int bytesDrained = counter += drain(s, data.getLast(),occupied,(int)Math.min(len,remaining));
		
		len-=bytesDrained;
		head+=bytesDrained;

		if (bytesDrained<remaining || len==0) {
			if (bytesDrained==remaining)
				data.add(recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop());
			
			return bytesDrained;
		}
			
		int fragments = (int)len/FRAGMENT_SIZE;
		int trailer = len%FRAGMENT_SIZE>0?1:0; 
		
		try {
			for (int i=0;i<fragments+trailer;i++,len-=FRAGMENT_SIZE) {
				byte[] fragment = recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop();
				data.add(fragment);
				
				bytesDrained = drain(s,fragment,0,(int)Math.min(FRAGMENT_SIZE,len));
				head+=bytesDrained;
				counter+=bytesDrained;
				
				if (FRAGMENT_SIZE>bytesDrained)
					return counter;
				
			}
		} finally {
			if (waiters>0)
				this.notifyAll();
		}
		
		data.add(recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop());
		return counter;
	}
	
	public synchronized int append(byte[] bytes, int offset, int len) throws IOException {
		int counter = 0;
		int occupied = head % FRAGMENT_SIZE; //amount of bytes already resident in the current storage fragment
		int remaining = FRAGMENT_SIZE-occupied; //amount of bytes that can be filled into the storage fragment
		
//		int bytesDrained = counter += 
//				drain(s, data.getLast(), occupied,(int)Math.min(len,remaining));
		
		int bytesDrained = counter += len<remaining?len:remaining;
		System.arraycopy(bytes, 0, data.getLast(), occupied, bytesDrained);
		
		len-=bytesDrained;
		head+=bytesDrained;

		if (bytesDrained<remaining || len==0) {
			if (bytesDrained==remaining)
				data.add(recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop());
			
			return bytesDrained;
		}
			
		int fragments = (int)len/FRAGMENT_SIZE;
		int trailer = len%FRAGMENT_SIZE>0?1:0; 
		
		try {
			for (int i=0;i<fragments+trailer;i++,len-=FRAGMENT_SIZE) {
				byte[] fragment = recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop();
				data.add(fragment);
				
				bytesDrained = FRAGMENT_SIZE<len?FRAGMENT_SIZE:len;
				System.arraycopy(bytes, counter, fragment, 0, bytesDrained);
				
				
				head+=bytesDrained;
				counter+=bytesDrained;
				
				if (FRAGMENT_SIZE>bytesDrained)
					return counter;
				
			}
		} finally {
			if (waiters>0)
				this.notifyAll();
		}
		
		data.add(recycled.isEmpty()?new byte[FRAGMENT_SIZE]:recycled.pop());
		return counter;
	}

	
	private int waiters=0;
	
//	public synchronized int read() {
//		if (closed)
//			return -1;
//		
//		while (head-tail<=0) try {
//			if (closed)
//				return -1;
//
//			waiters++;
//			this.wait();
//			waiters--;
//		} catch (InterruptedException ie) {
//			return 0;
//		}
//		
//		int padd = (tail % FRAGMENT_SIZE);
//		int total = closed?Math.min(head-tail,1):1;
//		
//		if (total==0)
//			return -1;
//		
//		int b = data.getFirst()[padd++];
//		
//		tail++;
//		
//		if (padd==FRAGMENT_SIZE)
//			recycled.add(data.pollFirst());
//		
//		return b;
//		int bytesRead = read(b,0,1);
//	}
	
	@Override
	@Deprecated
	public int read() throws IOException {
		byte buff[] = {0};
		read(buff);
		return buff[0];
	}

	public synchronized int read(byte[] b) {
		return read(b, 0, b.length);
	}
	
	public synchronized int read(byte[] b, int offset, int len) {
		if (closed)
			return -1;
		
		while (head-tail<=0) try {
			if (closed) 
				return -1;
			waiters++;
			this.wait();
			waiters--;
		} catch (InterruptedException ie) {
			return 0;
		}
		
		len = Math.min(head-tail, len);
		
		int counter = 0;
		int occupied = (tail % FRAGMENT_SIZE);
		int remaining = FRAGMENT_SIZE-occupied;
		
		int bytesRead = counter += Math.min(len, remaining);
		System.arraycopy(data.getFirst(), occupied, b, offset, bytesRead);
		
		len-=bytesRead;
		tail+=bytesRead;
		
		if (bytesRead<remaining || len==0) {
			if (bytesRead==remaining)
				recycled.add(data.pollFirst());
			
			return bytesRead;
		}
			
		int fragments = (int)len/FRAGMENT_SIZE;
		int trailer = len%FRAGMENT_SIZE>0?1:0; 
		
		try {
			for (int i=0;i<fragments+trailer;i++,len-=FRAGMENT_SIZE) {
				byte[] fragment = data.pollFirst();
				
				bytesRead = Math.min(FRAGMENT_SIZE,len);
				System.arraycopy(fragment, 0, b, offset, bytesRead);
				
				tail+=bytesRead;
				counter+=bytesRead;
				
				if (FRAGMENT_SIZE>bytesRead)
					return counter;
				else
					recycled.add(fragment);

			}
		} finally {
			if (waiters>0)
				this.notifyAll();
		}
		
		return counter;
	}
	
	synchronized public byte[] toByteArray() {
		byte[] buff = new byte[available()];
		read(buff);
		return buff;
	}
	
}
