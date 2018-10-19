package palmed;

import usrlib.Address;
import usrlib.Rec;

public class Practice {
	
	Rec practiceRec;
	
	
	
	Practice(){
		
	}
	
	
	Practice( Rec pRec ){
		
	}
	
	// TODO - hardcode for now
	
	
	public String getName(){
		return "Riverside Family Medicine";
	}

	public String getTaxID(){
		return "431769197";
	}

	public String getNPI(){
		return "1255610150";
	}
	
	
	public Address getAddress(){
		return new Address( "3129 Blattner Dr.", "", "Cape Girardeau", "MO", "63703", "573-335-0166", "573-335-7942", "" );
	}
	public String getEmail(){
		return "riversidefm@yahoo.com";
	}
	
	public String getContactName(){
		return "Margie Vargas";
	}
	

}
