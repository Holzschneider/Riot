package de.dualuse.commons;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Locale;

import de.dualuse.commons.io.CombinableFileFilter;
import de.dualuse.commons.io.PatternFileFilter;

public final class FileFilters {
	
	public static final FileFilter ALL = new java.io.FileFilter() {
		public boolean accept(File path) {
			return true;
		};
	};
	
	public static final FileFilter FILTER_DIRECTORY = new java.io.FileFilter() {
		public boolean accept(File path) {
			return path.isDirectory() && !path.getName().startsWith(".");
		}
	};
	
	public static final FileFilter FILTER_IMAGE = new java.io.FileFilter() {
		public boolean accept(File path) {
			String fileName = path.getName().toLowerCase(Locale.ENGLISH);
			return fileName.endsWith(".jpg") ||
					fileName.endsWith(".jpeg") ||
					fileName.endsWith(".png") ||
					fileName.endsWith(".tif") ||
					fileName.endsWith(".psd") ||
					fileName.endsWith(".pct");
		}
	};
	
	public static final FileFilter FILTER_JPG = new java.io.FileFilter() {
		public boolean accept(File path) {
			String fileName = path.getName().toLowerCase(Locale.ENGLISH);
			return fileName.endsWith(".jpg") ||
					fileName.endsWith(".jpeg");
		}
	};
	
	public static final FileFilter FILTER_PNG = new java.io.FileFilter() {
		public boolean accept(File path) {
			String fileName = path.getName().toLowerCase(Locale.ENGLISH);
			return fileName.endsWith(".png");
		}
	};
	
	public static final FileFilter FILTER_VIDEO = new java.io.FileFilter() {
		public boolean accept(File path) {
			String fileName = path.getName().toLowerCase(Locale.ENGLISH);
			return fileName.endsWith(".mov") ||
					fileName.endsWith(".mpg") ||
					fileName.endsWith(".mpeg") ||
					fileName.endsWith(".wmv") ||
					fileName.endsWith(".mkv") ||
					fileName.endsWith(".mp4") ||
					fileName.endsWith(".avi");
		}
	};
	
	public static final FileFilter FILTER_MEDIA = new java.io.FileFilter() {
		public boolean accept(File path) {
			return FILTER_IMAGE.accept(path) || FILTER_VIDEO.accept(path);
		}
	};
}
