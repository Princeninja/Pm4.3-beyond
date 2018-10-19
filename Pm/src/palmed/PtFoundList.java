package palmed;

import usrlib.Date;
import usrlib.Name;
import usrlib.Rec;

public class PtFoundList {

	private Name name;
    private Rec ptRec;
    private Date birthdate;
    private String ssn;
    
    
    public PtFoundList() {
    }

    public void setName(Name name) {
        this.name = name;
    }
    
    public Name getName() {
        return this.name;
    }
    
    
    
    public void setPtRec(Rec ptRec) {
        this.ptRec = ptRec;
    }
    
    public Rec getPtRec() {
        return this.ptRec;
    }
    
    

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
    
    public Date getBirthdate() {
        return this.birthdate;
    }
    
    

    public void setSSN(String ssn) {
        this.ssn = ssn;
    }

    public String getSSN() {
        return this.ssn;
    }
    
    

     /**
	 * toString
	 */
	public String toString() {
	    return ( this.name.getPrintableNameLFM() + "     " + birthdate.getPrintable());
	}
}
