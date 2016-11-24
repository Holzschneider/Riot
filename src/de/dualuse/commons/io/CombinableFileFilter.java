package de.dualuse.commons.io;

import java.io.File;
import java.io.FileFilter;


public abstract class CombinableFileFilter implements FileFilter {
	public CombinableFileFilter or(FileFilter f) { return new OrFileFilter(this, f); }
	public CombinableFileFilter and(FileFilter f) { return new AndFileFilter(this, f); }
	public CombinableFileFilter xor(FileFilter f) { return new XorFileFilter(this, f); }

	static class OrFileFilter extends CombinableFileFilter {
		private final FileFilter left, right;
		public OrFileFilter(FileFilter left, FileFilter right) { this.left = left; this.right = right; }
		public boolean accept(File pathname) { return left.accept(pathname)||right.accept(pathname); }
		public boolean accept(File dir, String filename) { return accept(new File(dir.getPath(),filename)); }
	}

	static class AndFileFilter extends CombinableFileFilter {
		private final FileFilter left, right;
		public AndFileFilter(FileFilter left, FileFilter right) { this.left = left; this.right = right; }
		public boolean accept(File pathname) { return left.accept(pathname)&&right.accept(pathname); }
		public boolean accept(File dir, String filename) { return accept(new File(dir.getPath(),filename)); }
	}

	static class XorFileFilter extends CombinableFileFilter {
		private final FileFilter left, right;
		public XorFileFilter(FileFilter left, FileFilter right) { this.left = left; this.right = right; }
		public boolean accept(File pathname) { return left.accept(pathname)^right.accept(pathname); }
		public boolean accept(File dir, String filename) { return accept(new File(dir.getPath(),filename)); }
	}
	
};