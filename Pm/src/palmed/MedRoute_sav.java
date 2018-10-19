package palmed;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MedRoute_sav {

	UNSPECIFIED	( 0, "",		"unspecified" ),
	AS_DIRECTED	( 1, "DIR",		"as directed" ),
	ORAL		( 2, "PO",		"by mouth" ),	
	EPIDURAL	( 3, "EPI",		"epidural" ),
	EARS		( 4, "Otic",	"in both ears" ),
	EYES		( 5, "Opht",	"in both eyes" ),
	NASAL		( 6, "Nasal",	"in the nose" ),
	PR			( 7, "PR",		"in the rectum" ),
	URETHRAL	( 8, "UR",		"in the urethra" ),
	VAGINAL		( 9, "Vag",		"in the vagina" ),
	IN_VITRO	( 10, "",		"in vitro" ),
	INHALED		( 11, "INH",	"inhale" ),
	IM			( 12, "IM",		"intramuscular inject" ),
	SQ			( 13, "SQ",		"inject below the skin" ),
	PENIS		( 14, "Penis",	"inject into the penis" ),
	SQ2			( 15, "SQ",		"inject into the skin" ),
	ARTERIAL	( 16, "Art",	"intraarterial" ),
	INTRAARTICULAR( 17,	"Artic",	"intraarticular" ),
	INTRAOCULAR	( 18, "",	"intraocular" ),
	INTRAPERITONEAL( 19, "IP", 	"intraperitoneal" ),
	INTRAPLEURAL( 20, "",		"intrapleural" ),
	INTRATHECAL	( 21, "IT",		"intrathecal" ),
	INTRAUTERINE( 22, "IU",		"intrauterine" ),
	IV			( 23, "IV",		"intravenous" ),
	INTRAVESICAL( 24, "",		"intravesical" ),
	TOPICAL		( 25, "TOP",	"apply to skin" ),
	TEETH		( 26, "TEETH",	"apply to teeth" ),
	TONGUE		( 27, "Tongue",	"on the tongue" ),
	PERFUSION	( 28, "perfusion",	"perfusion",				"" ),
	RINSE		( 29, "rinse",	"rinse",						"" ),
	UNDER_LIP	( 30, "under lip",	"under the lip",			"under the lip" ),
	SL			( 31, "SL",)	"sublingual",					"under the tongue",	"" ),
	L_EAR		( 32, "L Ear",	"L ear",						"in left ear",		"" ),	
	R_EAR		( 33, "R Ear",	"R ear",						"in right ear",		"" ),
	L_EYE		( 34, "L Eye",	"L eye",						"in left eye",		"" ),
	R_EYE		( 35, "R Eye",	"R eye",						"in right eye",		"" ),
	FACE		( 36, "face",	"apply to face",				"",					"" ),
	FACE		( 37, "face",	"thin layer to face",			"",					"" ),
	FEEDING_TUBE( 38, "tube",	"via feeding tube",				"",					"" ),
	eyelids
	eye_surgical
	lip
	nail
	scalp

	
	
	
	
	
	UNKNOWN( 99, "??", "?" );
	
	private int code;
	private String desc;
	private String abbr;

	private static final Map<String, Sex> lookup = new HashMap<String,Sex>();
	
	static {
		for ( Sex r : EnumSet.allOf(Sex.class))
			lookup.put(r.getAbbr(), r );
	}


	MedRoute_sav ( int code, String abbr, String desc ){
		this.code = code;
		this.desc = desc;
		this.abbr = abbr;
	}
	
	public String getLabel(){ return this.label; }
	public String getAbbr(){ return this.abbr; }
	public static Sex get( String abbr ){ return lookup.get( abbr ); }

}
