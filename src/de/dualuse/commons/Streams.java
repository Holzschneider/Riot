package de.dualuse.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import de.dualuse.commons.io.FastByteArrayOutputStream;

public class Streams {
	
	public static interface TransferProgressListener {
		void transferred(long total, long current);
		void done(long total);
	}
	
	public static byte[] drain(InputStream is) throws IOException {
		FastByteArrayOutputStream baos = new FastByteArrayOutputStream(1024);
		
		int total = pump(is, baos);
		return Arrays.copyOf(baos.getByteArray(),total);
	}
	

	public static int drain(InputStream in, byte[] buffer) throws IOException { return drain(in, buffer, 0, buffer.length); }
	
	public static int drain(InputStream in, byte[] buffer, int offset, int length) throws IOException {
		int totalBytesPumped = 0;
		for (int bytesRead = 0;bytesRead!=-1 && totalBytesPumped<length; totalBytesPumped += bytesRead = in.read(buffer,offset+totalBytesPumped, length-totalBytesPumped));
		return totalBytesPumped+1;
	}
	
	private static ThreadLocal<byte[]> transferBytes = new ThreadLocal<byte[]>() { protected byte[] initialValue() { return new byte[16*1024]; }; };
	
	public static int pump(InputStream in, OutputStream out) throws IOException {
		return pump(in, out, (TransferProgressListener)null);
	}
	
	public static int pump(InputStream in, OutputStream out, TransferProgressListener listener) throws IOException {
		return pump(in,out,transferBytes.get(), listener);
	}
	
	public static int pump(InputStream in, OutputStream out, byte[] transferBuffer) throws IOException {
		return pump(in, out, transferBuffer, (TransferProgressListener)null);
	}
	
	public static int pump(InputStream in, OutputStream out, byte[] transferBuffer, TransferProgressListener listener) throws IOException {
		int totalBytesPumped = 0;
		
		for (int bytesRead = 0; bytesRead!=-1; totalBytesPumped += bytesRead = in.read(transferBuffer)) {
			out.write(transferBuffer,0,bytesRead);
			if (bytesRead!=-1 && listener!=null) {
				listener.transferred(totalBytesPumped, bytesRead);
			}
		}
		
		// Compensates EOF: read() returns -1, which gets added to totalBytesPumped in the last iteration
		totalBytesPumped = totalBytesPumped + 1;
		
		if (listener != null) listener.done(totalBytesPumped);
		
		return totalBytesPumped; 
	}
	
	// Only read at most 'length' bytes, don't wait for read() to return -1
	public static int pump(InputStream in, OutputStream out, byte[] transferBuffer, int length) throws IOException {
		int totalBytesPumped = 0;
		
		for (int bytesRead = 0; bytesRead!=-1; totalBytesPumped += bytesRead = in.read(transferBuffer, 0, Math.min(transferBuffer.length, length-totalBytesPumped))) {
			out.write(transferBuffer,0,bytesRead);
			
			if (totalBytesPumped == length && bytesRead>-1) {
				totalBytesPumped--;
				break;
			}
		}
		
		// Compensates EOF: read() returns -1, which gets added to totalBytesPumped in the last iteration
		totalBytesPumped = totalBytesPumped + 1;
		
		return totalBytesPumped;
	}
	
	
	///////
	
	
	private static ThreadLocal<char[]> transferCharacters = new ThreadLocal<char[]>() { protected char[] initialValue() { return new char[16*1024]; }; };
	
	public static String drain(Reader r) throws IOException { 
		StringBuilder s = new StringBuilder();
		char[] buffer = transferCharacters.get();
		
		for (int charsRead = 0; charsRead>=0; charsRead = r.read(buffer))
			s.append(buffer,0,charsRead);
		
		return s.toString();
	}
	
	public static int drain(Reader in, char[] buffer) throws IOException { return drain(in, buffer, 0, buffer.length); }
	
	public static int drain(Reader in, char[] buffer, int offset, int length) throws IOException {
		int totalBytesPumped = 0;
		for (int bytesRead = 0;bytesRead!=-1 && totalBytesPumped<length; totalBytesPumped += bytesRead = in.read(buffer,offset+totalBytesPumped, length-totalBytesPumped));
		return totalBytesPumped+1;
	}
	
	
	public static int pump(Reader in, Writer out) throws IOException { return pump(in,out,transferCharacters.get()); }
	
	public static int pump(Reader in, Writer out, char[] transferBuffer) throws IOException {
		int totalBytesPumped = 0;
		for (int bytesRead = 0;bytesRead!=-1; totalBytesPumped += bytesRead = in.read(transferBuffer))
			out.write(transferBuffer,0,bytesRead);
		
		return totalBytesPumped+1;
	}
	
}
