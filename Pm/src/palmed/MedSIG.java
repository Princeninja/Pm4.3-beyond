package palmed;

public class MedSIG {
	
	
	
	public static String getPrintableSIG( Cmed cmed ){
		
		if ( cmed == null ) return "";
		
		String action = "";
		String dose = MedDosage.getDesc( cmed.getDosage());
		String form = MedForm.getDesc( cmed.getForm());
		String route = MedRoute.getDesc( cmed.getRoute());
		String freq = MedFreq.getDesc( cmed.getSched());
		
		String sig = dose + " " + form + " " + route + " " + freq;
		String add = cmed.getAddlSigTxt();
		if ( add.length() > 0 ) sig = sig + "; " + add;
		return sig;
	}

}
