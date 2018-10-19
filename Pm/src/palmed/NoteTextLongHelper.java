package palmed;

import usrlib.Date;
import usrlib.Rec;

/*

// Example Usage

class ObNotes extends NoteTextHelper {
	@Override
    public String getFnsub(){ return "test%02d.med"; }
	@Override
	public void setReca( Reca reca ){ setObNoteReca( reca ); }
	@Override
	public Reca getReca(){ return getObNoteReca(); }
	@Override
	public Rec getRefRec(){ return getPtRec(); }
}

ObNotes obNotes = null;


// Then in the using class's constructor:
obNotes = new ObNotes();

// In the read() function call:
obNotes.resetReadStatus();

// In the write() and writeNew(), before the main record is written, call:
obNotes.write();

// And make the getters/setters like this:
public String getObNoteText(){ return obNotes.getNoteText(); }
// and
public void setObNoteText( String str ){ return obNotes.setNoteText( str ); }


*/



public class NoteTextLongHelper {

	private int vol;
    private String noteText = null;				// additional note text
    private boolean flgNoteTxt = false;			// flag that note text has been set
    private boolean flgNoteTxtRead = false;		// flag that note text has been read
  
    //private String fnsub;				// file name substring    
    //private Rec ptRec = null;
    
    
    
    /**
     * NoteTextHelper()
     * 
     * Public constructor.
     * 
     */
    public NoteTextLongHelper(){
    }
    
    
    
    
    

    //
    // these methods should be overridden by an extending class
    //
    
    public Rec getRefRec(){ return new Rec( 0 ); }    
    public String getFnsub(){ return null; }
    public Rec getRec(){ return null; }
    public void setRec( Rec rec ){ return; }

    
    
    
    
    
    // public void setVol()
    
    public void setVol( int vol ){
    	this.vol = vol;
    }
    
    
    
    
    /**
     * resetReadStatus()
     * 
     * Reset the flgNoteTxtRead flag.  Call this function from the using class's read() method.
     * 
     * @param void
     * @return void
     */
    public void resetReadStatus(){
    	this.flgNoteTxtRead = false;
    }
    
    
    
    
    /**
     * write()
     * 
     * Write the note text.  Call this method from the using class's write method.
     * 
     * @param void
     * @return void
     */
    public void write(){
    
		// handle note text
		if ( flgNoteTxt ){
			Rec rec1 = this.writeNoteText( noteText );
			this.setRec( rec1 );
			flgNoteTxt = false;
		}
		return;
    }

    
    
    
    /**
     * getNoteTxt()
     * 
     * Get note text.  Call this method from the using class's getter function.
     * 
     * @param none
     * @return String
     */
    public String getNoteText(){
    	
    	// have we read note text record yet, if not - read it
    	if ( ! flgNoteTxtRead ){
    		
    		Rec rec = this.getRec();
    		if ( Rec.isValid(rec)){
    			this.noteText = this.readNoteText( rec );
    			flgNoteTxtRead = true;
    		}
    	}
    	
    	return ( this.noteText == null ) ? "": this.noteText;
    }

    
    
    /**
     * setNoteTxt()
     * 
     * Set note text.  Call this method from the using class's setter function.
     * 
     * @param String
     * @return void
     */
    public void setNoteText( String txt ){
    	
    	// does he have note saved already?  read it.
    	if ( ! this.flgNoteTxtRead && Rec.isValid( this.getRec())){
    		this.readNoteText();
    	}
    	
    	// handle trivial cases
    	if (( txt == null ) || (( txt = txt.trim()).length() < 1 )){
    		// if there is already a note
    		if ( this.flgNoteTxtRead && ( this.noteText.length() > 0 )){
    			// set note to no text
    			this.noteText = "";
    			this.flgNoteTxt = true;
    		}
    		return;
    	}

    	
    	// are the new and old the same?
    	//   if yes, nothing to do
    	if ( txt.equals( this.noteText )) return;
    	
    	// set text and mark to be written
    	this.flgNoteTxt = true;
    	this.noteText = txt;
    }


    
    
    
    
    //
    // These functions call NoteText to read and write the note.
    //
    
    
    private String readNoteText( Rec rec ){
    	
    	// handle trivial case
    	if (( rec == null ) || ( ! rec.isValid())) return "";
    	// read entry from text file
    	String s = NoteTextLong.readText( getFnsub(), vol, rec );    	   	
		return s.trim();
    }

    private String readNoteText(){ 
		this.noteText = readNoteText( this.getRec() );
		flgNoteTxtRead = true;
    	return this.noteText;
    }
    
    private Rec writeNoteText( String text ){
    	if ( text == null ) text = "";    	
    	Rec rec = NoteTextLong.saveTextLong( this.getFnsub(), vol, Date.today(), getRefRec(), this.getRec(), text );
    	return rec;
    }


    	

    
    
}
