package de.dualuse.commons;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Strings {
	
	static final public String LEXICOGRAPHIC_TIMESTAMP_FORMAT = "yyyy-MM-dd HH'h'mm'm'ss.ms's'";
	static final public String HTTP_TIMESTAMP_FORMAT = 	"EEE, dd MMM yyyy HH:mm:ss z";

	static private SimpleDateFormat dateFormatter = null;
	
	public static String format(Date d) { return (dateFormatter==null?dateFormatter=new SimpleDateFormat(LEXICOGRAPHIC_TIMESTAMP_FORMAT, Locale.US):dateFormatter).format(d); }
	
	public static String format(double value) {
		return format(value,2);
	}
	
	public static String format(double value, int digits) {
		return String.format(Locale.US,"%."+digits+"f", value);
	}
	
	
	public static String format(long timeValue, TimeUnit tu) {
		long time = tu.toMillis(timeValue);
		
		String prefix = "";
		if (time < 0) {
			time = -time;
			prefix = "-";
		}
		
		int tmp = (int)(1000*time);
		int milliseconds = tmp%1000;
		tmp = tmp/1000;
		int seconds = tmp%60;
		tmp = tmp/60;
		int minutes = tmp%60;
		tmp = tmp/60;
		int hours = tmp%60;
		
		return prefix + String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
	}
	
	
	
	/////////////////
	
	/**
	 * TODO mature this! achtung, beim nicht-erkennen rollback zum ersten Zeichen+1 
	 * 
	 * @param template
	 * @param keys sorted keys
	 * @param replacements
	 * @return
	 */
	public static String replaceWithSortedKeys(String template, String keys[], Object replacements[]) {
		int n = keys.length, keySum = 0, replacementSum = 0;
		for (int i=0;i<n;i++) {
			keySum += keys[i].length();
			replacementSum += replacements[i].toString().length(); 
		}
		
		StringBuilder replaced = new StringBuilder(template.length()*keySum*4/replacementSum/3);
		
		int cursor = 0;
		outer:
		for (int i=0,I=template.length();i<I;) {
			char c = template.charAt(i++);
			
			int k=0, K=n-1, l=k, u=K;
			inner:
			for (int j=0;;j++) {
				while (k<=K) {
					int m = (k+K)/2;
					char d = keys[m].charAt(j);
					if (d<c) k = m+1;
					else if (d>c) K = m-1;
					else {
						for (k=m;k-1>=l && keys[k-1].charAt(j)==c;k--);
						for (K=m;K+1<=u && keys[K+1].charAt(j)==c;K++);
						for (m=k;m<=K;m++) 
							if (keys[m].length()==j+1) {
								replaced.append(template, cursor, i-j-1).append(replacements[m].toString());
								cursor = i;
								break inner;
							}
						
						l=k;
						u=K;
						c = template.charAt(i++);
						continue inner;
					}
				}
				continue outer;
			}
		}
		
		return replaced.append(template,cursor, template.length()).toString();
	}
	
//	public static <T extends Object> String replace(String template, Collection<Map.Entry<String, T>> dictionary) {
//		
//		ArrayList<Map.Entry<String,String>> sortableReplacements = new ArrayList<Map.Entry<String,String>>(); 
//		
//		String keys[] = new String[sortableReplacements.size()], replacements[] = new String[keys.length];
//		
//		for (int i=0,I=sortableReplacements.size();i<I;i++) {
//			Map.Entry<String,String> e = sortableReplacements.get(i);
//			keys[i] = e.getKey();
//			replacements[i] = e.getValue();
//		}
//		
//		return replaceWithSortedKeys(template, keys, replacements).toString();
//	}
	
//	public static <T extends Object> String replace(String template, Map<String, T> replacements) {
//		return replace(template, replacements.entrySet());
//	}
	
	
//	public static <T> void quicksort(T[] elements, Comparator<? super T> c) { quicksort(elements, 0, elements.length-1, c); }
//	public static <T> void quicksort(T[] elements, int low, int high, Comparator<? super T> c) {
//		int i = low, j = high;
//		T pivot = elements[(low + high) / 2];
//
//		while (i <= j) {
//			while (c.compare(elements[i],pivot)<0) i++;
//			while (c.compare(elements[j],pivot)>0) j--;
//			if (!(i <= j)) continue;
//			T t = elements[i];
//			elements[i]=elements[j];
//			elements[j]=t;
//			i++;
//			j--;
//		}
//		if (low < j) quicksort(elements,low,j,c);
//		if (i < high) quicksort(elements,i,high,c);
//	}

	
	

	public static void main(String[] args) {		
		
		String template = "hello is red";
		
//		HashMap<String,String> dictionary = new LinkedHashMap<String, String>();
//		dictionary.put("hello","world");
//		dictionary.put("is","was");
//		dictionary.put("red", "green");
//		String replaced = Strings.replace("hello is red", dictionary);
//		System.out.println(replaced);
		
		
		String[] keys = {"hallo", "hello", "hello", "is", "red"};
		String[] replacements = {"welt","heaven","world", "was", "green"};
		
		
		Arrays.sort(keys);
		
		
		String replaced = Strings.replaceWithSortedKeys(template, keys, replacements);
		System.out.println(replaced);
		
		
	}
}



