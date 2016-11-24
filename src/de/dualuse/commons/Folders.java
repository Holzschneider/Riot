package de.dualuse.commons;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Folders {

	public static interface CopyProgress {
		void update(long bytes);
		boolean abort();
	}
	
	// Delete directory recursively.
	public static boolean delete(File dir) {
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				boolean success = delete(file);
				if (!success) return false;
			}
		}
		return dir.delete();
	}

	/**
	 * Relativizes a path such that
	 * &nbsp;new File(from, relativize(from, to).getPath ).equals ( to );
	 * 
	 * @param fromFolder (folder)
	 * @param toFile (file)
	 * @return
	 * @throws IOException
	 */
	public static File relativize(File fromFolder, File toFile) throws IOException {
		
		String cFrom[] = fromFolder.getCanonicalPath().split(Pattern.quote(File.separator)); // Pattern.quote(..) to escape windows '\' separator
		String cTo[] = toFile.getCanonicalPath().split(Pattern.quote(File.separator));
		
		int common = 0;
		for (int max = cFrom.length<cTo.length?cFrom.length:cTo.length;common < max && cFrom[common].equals(cTo[common]);common++);

		String resultPath = toFile.getName();
		for (int i=cTo.length-2;i>common-1;i--)
			resultPath = new File(cTo[i],resultPath).getPath();
		
		int commonFrom = cFrom.length-common;
		
		for (int i=0;i<commonFrom;i++)
			resultPath = new File("..",resultPath).getPath();
		
		return new File(resultPath);
	}
	
	
	public static List<File> find(File sourceDirectory) {
		return find(sourceDirectory, FileFilters.ALL , new ArrayList<File>());
	}
	
	public static List<File> find(File sourceDirectory, FileFilter filters) {
		return find(sourceDirectory, filters, new ArrayList<File>());
	}
	
	public static<Collection extends java.util.Collection<? super File>> Collection find(File sourceDirectory, FileFilter filters, Collection fileList) {
		if (sourceDirectory.isDirectory()) {
			File[] files = sourceDirectory.listFiles();
			if (files!=null)
				for(File file : files) {
					if(filters.accept(file)) 
						fileList.add(file);
	
					if(file.isDirectory())
						find(file, filters, fileList);
				}
		}
		return fileList;
	}

	
	
	public static void copy(File source, File dest) throws IOException { copy(source, dest, null); }
	public static void copy(File source, File dest, CopyProgress listener) throws IOException {
		if (!source.isDirectory())
			if (listener==null) copyFile(source, dest);
			else copyFile(source,dest,listener);
		
		if (isAncestorOf(source, dest)) throw new IOException("Destination is a subdirectory of the source directory (recursion).");
		if (source.equals(dest)) return;
		copyDirRecursively(source, dest, listener);
	}
	
	private static void copyDirRecursively(File source, File dest, CopyProgress listener) throws IOException {
		if (listener != null) if (listener.abort()) return;
		
		if (source.isDirectory()) {
			if (dest.exists() && !dest.isDirectory()) throw new IOException("Couldn't create folder.");
			dest.mkdirs();
			
			for (File file : source.listFiles()) {
				File fileDest = new File(dest, file.getName());
				copyDirRecursively(file, fileDest, listener);
			}
		} else {
			if (listener != null) {
				copyFile(source, dest, listener);
			} else {
				copyFile(source, dest);
			}
		}
	}
	

	private static void copyFile(File in, File out) throws IOException {
		if (in.getCanonicalFile().equals(out.getCanonicalFile()))
			return;
		
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		
		try {
			inChannel = new FileInputStream(in).getChannel();
			outChannel = new FileOutputStream(out).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			
			if (outChannel != null)
				outChannel.close();
		}
	}
	

	private static void copyFile(File in, File out, CopyProgress listener) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		
		byte[] buffer = new byte[4096];
		int len = -1;
		
		try {
			is = new FileInputStream(in);
			os = new FileOutputStream(out);
			
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				if (listener != null) {
					listener.update(len);
					if (listener.abort()) return;
				}
			}
			
		} finally {
			try { if (is != null) is.close(); }
			finally { if (os != null) os.close(); }
		}
	}
	

	
	
	public static boolean isAncestorOf(File ancestor, File descendant) {
		while (descendant.getParentFile() != null) {
			descendant = descendant.getParentFile();
			if (ancestor.equals(descendant)) return true;
		}
		return false;
	}
	

	// Compute byte size of directory tree
	public static long size(File dir) {
		if (!dir.exists()) return 0;
		if (!dir.isDirectory()) return dir.length();
		long sum = 0; for (File child : dir.listFiles()) sum += size(child);
		return sum;
	}
	
}
