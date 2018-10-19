package usrlib;

import java.io.RandomAccessFile;

import palmed.SystemHelpers;

public class RecaFile {
	
    private RandomFile file;
    private String fnsub;

    
    private RecaFile( String path, String fnsub, int vol, int rlen, RandomFile.Mode fmode ){
    	
    	this.fnsub = fnsub;
    	String fname = String.format( fnsub, vol );
    	this.file = RandomFile.open(path, fname, rlen, fmode);
    	return;
    }
    
 
    
    
    
    /**
     * open()
     * 
     * This STATIC method opens a file and returns a new RecaFile object.
     * 
     * @param String path
     * @param fnsub
     * @param vol
     * @param rlen
     * @param fmode
     * @return RecaFile
     */
    public static RecaFile open( String path, String fnsub, int vol, int rlen, RandomFile.Mode fmode ){
    	
    	return new RecaFile( path, fnsub, vol, rlen, fmode );
    }
   
    
    
    

    
    /**
     * close()
     * 
     * This method closes the RandomFile associated with RecaFile.
     * 
     * @param none
     * @return void
     */
    public void close(){
    	this.file.close();
    	return;
    }
    
    
    //TODO  get and put, etc
    
    
    
    /**
     * readRec()
     * 
     * This method opens a file, reads a record, closes the file, and returns.
     * 
     * 
     */
    public static boolean readReca( Reca reca, byte[] data, String path, String fnsub, int rlen ){

    	//System.out.println( "Reca.readReca() fnsub=" + fnsub + ", vol=" + reca.getYear() % 100 );
    	String fname = String.format( fnsub, reca.getYear() % 100 );
    	//System.out.println( "Reca.readReca() fname=" + fname );
    	RandomFile file = RandomFile.open( path, fname, rlen, RandomFile.Mode.READONLY );
    	if ( file == null ) return false;  	
    	boolean ret = file.getRecord( data, reca.getRec());
    	file.close();
    	return ret;
    }
    
    
    
    
    
    
    
    /**
     * writeReca()
     * 
     * This method opens a file, writes a record, closes the file, and returns.
     * 
     * 
     */
    public static boolean writeReca( Reca reca, byte[] data, String path, String fnsub, int rlen ){

    	if (( reca == null ) || ( reca.getRec() < 2 )) SystemHelpers.seriousError( "RecaFile.writeReca) bad reca" );
    	String fname = String.format( fnsub, reca.getYear() % 100 );
    	RandomFile file = RandomFile.open( path, fname, rlen, RandomFile.Mode.READWRITE );
    	if ( file == null ) return false;  	
    	//TODO handle file open abort retry ignore
    	boolean ret = file.putRecord( data, reca.getRec());
    	file.close();
    	return ret;
    }
    
    
    
    
    
    /**
     * newReca()
     * 
     * This method opens a file, writes a record, closes the file, and returns.
     * 
     * @param int vol;			// volume number (2 digit year)
     * 
     * @return Reca
     */
    public static Reca newReca( int vol, byte[] data, String path, String fnsub, int rlen ){
    	
    	Reca reca;			// new reca
    	
    	String fname = String.format( fnsub, vol % 100 );
    	RandomFile file = RandomFile.open( path, fname, rlen, RandomFile.Mode.READWRITE );
    	if ( file == null ) return null;  	
    	//TODO handle file open abort retry ignore
    	reca = new Reca( vol, file.allocateRecord());
    	boolean ret = file.putRecord( data, reca.getRec());
    	file.close();
    	return reca;
    }


}
