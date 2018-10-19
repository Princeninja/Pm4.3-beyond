package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Race {

	CAUCASIAN( "Caucasian", "C" ),	
	BLACK( "Black", "B" ),
	ASIAN( "Asian", "A" ),
	HISPANIC( "Hispanic", "H" ),
	NATIVE( "Native American", "N" ),
	MIXED( "Mixed Race", "M" ),
	INDIAN( "Asian Indian", "I" ),
	ABORIGINE( "Australian Aborigine", "E" ),
	HAWAIIAN( "Hawaiian or Pacific Islander", "P" ),
	OTHER( "Other", "O" ),
	UNSPECIFIED( "??", "?" );
	
	private String label;
	private String abbr;

	private static final Map<String, Race> lookup = new HashMap<String,Race>();
	
	static {
		for ( Race r : EnumSet.allOf(Race.class))
			lookup.put(r.getAbbr(), r );
	}


	Race ( String label, String abbr ){
		this.label = label;
		this.abbr = abbr;
	}
	
	public String getLabel(){ return this.label; }
	public String getAbbr(){ return this.abbr; }
	public static Race get( String abbr ){ return lookup.get( abbr ); }
}
