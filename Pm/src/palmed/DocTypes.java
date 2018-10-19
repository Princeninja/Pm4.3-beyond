package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum DocTypes {
	
	DEFAULT( 0, "???", "Unknown", "xxx" ),
	TIFF( 1, "TIFF", "TIFF Image", "tif" ),
	TEXT( 2, "Text", "ASCII Text", "txt" ),
	WPD( 3, "WP", "WordPerfect Document", "wpd" ),
	BMP( 4, "BMP", "BMP Image", "bmp" ),
	JPEG( 5, "JPEG", "JPEG Image", "jpg" ),
	GIF( 6, "GIF", "GIF Image", "gif" ),
	WAV( 7, "WAV", "WAV Audio File", "wav" ),
	DFAX( 8, "DFAX", "DigiFax Image", "fax" ),
	WORD( 9, "Word", "Word Document", "doc" ),
	PDF( 10, "PDF", "PDF Document", "pdf" ),
	PS( 11, "PS", "Postscript Document", "ps" ),
	HTML( 12, "HTML", "HTML Document", "html" );
	
	private static final Map<Integer, DocTypes> lookup = new HashMap<Integer,DocTypes>();
	
	static {
		for ( DocTypes s : EnumSet.allOf(DocTypes.class))
			lookup.put(s.getCode(), s );
	}
	
	private int code;			// int code
	private String abbr;		// abbreviation
	private String desc;		// description
	private String ext;			// file name extension
	
	private DocTypes( int code, String abbr, String desc, String ext ){
		this.code = code;
		this.abbr = abbr;
		this.desc = desc;
		this.ext = ext;
	}
	
	public int getCode() { return code; }
	public String getAbbr() { return abbr; }
	public String getDesc() { return desc; }
	public String getExt() { return ext; }
	
	public static DocTypes get( int code ){ return lookup.get( code ); }
}