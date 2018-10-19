package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Language {

	ENGLISH( "English", 1 ),	
	SPANISH( "Spanish", 2 ),
	ARABIC( "Arabic", 3 ),
	CHINESE( "Chinese", 4 ),
	CROATIAN( "Croatian", 5 ),
	DUTCH( "Dutch", 6 ),
	FILIPINO( "Filipino", 7 ),
	FRENCH( "French", 8 ),
	GERMAN( "German", 9 ),
	GREEK( "Greek", 10 ),
	HEBREW( "Hebrew", 11 ),
	HINDI( "Hindi", 12 ),
	IRISH( "Irish", 13 ),
	ITALIAN( "Italian", 14 ),
	JAPANESE( "Japanese", 15 ),
	KOREAN( "Korean", 16 ),
	PERSIAN( "Persian", 17 ),
	POLISH( "Polish", 18 ),
	PORTUGUESE( "Portuguese", 19 ),
	RUSSIAN( "Russian", 20 ),
	SERBIAN( "Serbian", 21 ),
	UNLISTED( "Unlisted", 99 ),
	UNSPECIFIED( "Unspecified", 0 );

	private String label;
	private int code;

	private static final Map<Integer, Language> lookup = new HashMap<Integer,Language>();
	
	static {
		for ( Language r : EnumSet.allOf(Language.class))
			lookup.put(r.getCode(), r );
	}


	Language ( String label, int code ){
		this.label = label;
		this.code = code;
	}
	
	public String getLabel(){ return this.label; }
	public int getCode(){ return this.code; }
	public static Language get( int code ){ return lookup.get( code ); }

}
