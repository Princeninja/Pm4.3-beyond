package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Sex {
	
	MALE( "Male", "M" ),	
	FEMALE( "Female", "F" ),
	UNKNOWN( "??", "?" );
	
	private String label;
	private String abbr;

	private static final Map<String, Sex> lookup = new HashMap<String,Sex>();
	
	static {
		for ( Sex r : EnumSet.allOf(Sex.class))
			lookup.put(r.getAbbr(), r );
	}


	Sex ( String label, String abbr ){
		this.label = label;
		this.abbr = abbr;
	}
	
	public String getLabel(){ return this.label; }
	public String getAbbr(){ return this.abbr; }
	public static Sex get( String abbr ){ return lookup.get( abbr ); }

}
