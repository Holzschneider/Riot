package de.dualuse.commons.io;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import de.dualuse.commons.io.CombinableFileFilter.AndFileFilter;
import de.dualuse.commons.io.CombinableFileFilter.OrFileFilter;
import de.dualuse.commons.io.CombinableFileFilter.XorFileFilter;


public class PatternFileFilter extends CombinableFileFilter {
	final private Pattern pattern;

	public PatternFileFilter(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	
	public boolean accept(File f) {
		return pattern.matcher(f.getName()).matches();
	}
	
	
	static public PatternFileFilter with(String pattern) {
		return new PatternFileFilter(pattern);
	}
	
	public PatternFileFilter or(String regex) { return new PatternFileFilter( "("+pattern.toString()+")|("+regex+")" ); }
	public PatternFileFilter and(String regex) { return new PatternFileFilter( "("+pattern.toString()+")&("+regex+")" ); }
	
}
