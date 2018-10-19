package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Ethnicity {

	NOT_HISPANIC( "Not Hispanic or Latino", 1 ),	
	HISPANIC( "Hispanic", 2 ),
	UNLISTED( "Unlisted", 99 ),
	UNSPECIFIED( "Unspecefied", 0 );
	
	private String label;
	private int code;

	private static final Map<Integer, Ethnicity> lookup = new HashMap<Integer,Ethnicity>();
	
	static {
		for ( Ethnicity r : EnumSet.allOf(Ethnicity.class))
			lookup.put(r.getCode(), r );
	}


	Ethnicity ( String label, int code ){
		this.label = label;
		this.code = code;
	}
	
	public String getLabel(){ return this.label; }
	public int getCode(){ return this.code; }
	public static Ethnicity get( int code ){ return lookup.get( code ); }


}
