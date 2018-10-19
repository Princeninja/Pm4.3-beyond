package palmed;

public class SNOMED {
	
	
	
	
	
	
	// palmed.Sex
	public static String getSNOMED( palmed.Sex sex ){
		if ( sex == palmed.Sex.MALE ) return "248153007";
		if ( sex == palmed.Sex.FEMALE ) return "248152002";
		return "";
	}

	// palmed.Race
	public static String getSNOMED( palmed.Race race ){
		if ( race == palmed.Race.CAUCASIAN ) return "413773004";
		if ( race == palmed.Race.BLACK ) return "413464008";
		if ( race == palmed.Race.ASIAN ) return "413581001";
		if ( race == palmed.Race.INDIAN ) return "414481008";
		if ( race == palmed.Race.NATIVE ) return "413490006";
		if ( race == palmed.Race.MIXED ) return "414752008";
		if ( race == palmed.Race.HISPANIC ) return "414408004";
		if ( race == palmed.Race.ABORIGINE ) return "413600007";
		if ( race == palmed.Race.OTHER ) return "415226007";	// code for unspecified
		if ( race == palmed.Race.UNSPECIFIED ) return "415226007";
		return "";
	}

}
