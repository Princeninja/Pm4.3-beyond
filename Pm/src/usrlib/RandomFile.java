package usrlib;

import java.io.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import palmed.SystemHelpers;


//TODO - Handle errors


/**
 * <p>Title: PAL/MED</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 1983 - 2005</p>
 *
 * <p>Company: PAL/MED</p>
 *
 * @author J. Richard Palen
 * @version 5.0
 */
public class RandomFile {

	public enum Mode {
		
		READONLY( "readonly", "r" ),	
		READWRITE( "readwrite", "rw" ),
		CREATE( "create", "rw" ),
		CREATE_NEW( "create_new", "rw" );
		
		private String label;
		private String code;

		private static final Map<String, Mode> lookup = new HashMap<String,Mode>();
		
		static {
			for ( Mode r : EnumSet.allOf(Mode.class))
				lookup.put(r.getCode(), r );
		}


		Mode ( String label, String code ){
			this.label = label;
			this.code = code;
		}
		
		public String getLabel(){ return this.label; }
		public String getCode(){ return this.code; }
		public static Mode get( String code ){ return lookup.get( code ); }
	}

	

	// FIELDS
	
    private RandomAccessFile randomFile;
    private int recLength;
    private String fileName;
    private String pathName;
    private Mode mode;

    
    
    
    
    // CONSTRUCTORS
    
    /**
     * RandomFile
     *
     * @param pathString String
     * @param fileName String
     * @param recLength int
     * @param fileMode String
     */
    public RandomFile(String pathString, String fileName, int recLength, Mode mode) {

        this.recLength = recLength;
        this.fileName = fileName;
        this.pathName = pathString;
        this.mode = mode;

        File file = new File( pathString, fileName );
        doOpen( file, mode );
    }

    public RandomFile( File file, Mode mode ){
    	this.recLength = 1;
    	this.fileName = file.getName();
    	this.pathName = file.getParent();
    	this.mode = mode;
    	doOpen( file, mode );
    }
    
    private RandomFile(){
    	
    }

    private boolean doOpen( File file, Mode mode ){
    	
    	boolean flgRetry = true;
    	
    	while ( flgRetry ){
    		flgRetry = false;
        	boolean flgExists = file.exists();

	    	if (( mode == Mode.READWRITE ) || ( mode == Mode.READONLY )){

	    		// does file exist?
	    		if ( ! file.exists() ){
	    			switch ( DialogHelpers.Optionbox( "File Not Found", "Error opening file '" + this.fileName + "' in path '" + this.pathName + "' due to 'File Not Found'.  What would you like to do?", "Retry", "Ignore", "Create")){
	    			//switch ( DialogHelpers.Optionbox( "File Not Found", "Error opening file: 'File Not Found'.\n  File: " + this.fileName + "\nPath: " + this.pathName + "\n\n   What would you like to do?", "Retry", "Ignore", "Create")){
	                case 1:
	                	// retry
	                	flgRetry = true;
	                	continue;
	                	
	                case 2:		// abort/ignore
	                	return false;
	                	
	                case 3:		// create file and initialize first record
	                	try {
	    					randomFile = new RandomAccessFile( file, "rw" );
	    					this.init();					
	    					this.close();    					
	    				} catch ( IOException e1 ){
	    					SystemHelpers.seriousError( "File open error: " + this.pathName + "/" + this.fileName );
	    				}
	    				break;
	    			}
	    		}
	    	}
	    	
	    	if ( mode == Mode.CREATE_NEW ){
	    		if ( flgExists ){
	    			DialogHelpers.Messagebox( "File already exists: " + this.fileName );
	    		}
	    	}
	    	
	    	// all modes need read permission
	    	if ( flgExists ){
	    		
		    	while ( true ){	    		
	    			if ( file.canRead()) break;
	    			if ( DialogHelpers.Optionbox( "File Permission Error", "Error opening file due to 'File Read Permissions'.  What would you like to do?", "Retry", "Ignore", null ) != 1 ){
	    				return false;
	    			}
		    	}
		  
		    	// check for write permission
		    	if (( mode == Mode.READWRITE )){
		    		while ( true ){
		    			if ( file.canWrite()) break;
		    			if ( DialogHelpers.Optionbox( "File Permission Error", "Error opening file due to 'File Write Permissions'.  What would you like to do?", "Retry", "Ignore", null ) != 1 ){
		    				return false;
		    			}
		    		}
		    	}
	    	}
	    	
	    		
	    		
	    	
	        try { 
	        	randomFile = new RandomAccessFile( file, /*this.pathName+ "/" + fileName,*/ mode.getCode());
	        } catch ( Exception e ){
	            // some kind of file open error
	        	SystemHelpers.seriousError( "File open error: " + e.getMessage() + ", " + this.pathName + "/" + this.fileName );
	            System.out.println( "File open error: " + e.getMessage() + ", " + this.pathName + "/" + this.fileName );
	            return false;
	        }
    	}
    	
    	return true;
    }
    
    
    
    /**
     * getRandomAccessFileObj()
     * 
     * @param none
     * @return RandomAccessFile object
     */
    public RandomAccessFile getRandomAccessFileObj() {
        return randomFile;
    }
    
    
    
    
    
    
    
    /**
     * open()
     * 
     * This STATIC method opens a file and returns a new RandomFile object.
     * 
     * This can be a great entry point.
     * 
     * @param String path
     * @param String fname
     * @param int rlen
     * @param String fmode
     * @return new RandomFile object
     */
    public static RandomFile open( String path, String fname, int rlen, Mode fmode ){
    	return new RandomFile( path, fname, rlen, fmode );
    }
    
    
    
    
    
    
    /**
     * close
     */
    public void close() {
        try {
            if ( randomFile != null ) randomFile.close();
        } catch (IOException ex) {
            // some kind of file close error
            System.out.println( "File close error: " + ex.getMessage());
        }
    }
    
    
    
    
    
    

    /**
     * length
     */
    public long length() {
        long length = -1;
        try {
            length = randomFile.length();
        } catch (IOException ex) {
            // some kind of file read error
            System.out.println( "File get length error: " + ex.getMessage());
        }
        return length;
    }


    
    
    
    /**
     * seek()
     * 
     * @param long
     * @return none
     */
    public void seek( long offset ) {
        try {
            randomFile.seek( offset );
        } catch (IOException ex) {
            // some kind of file error
            System.out.println( "File seek error: " + ex.getMessage());
        }
    }
    
    
    
    
    
    
    /**
     * seekToEnd()
     * 
     * @param none
     * @return long
     */
    public long seekToEnd() {
    	
    	long length = 0;
    	
        try {
        	length = randomFile.length();
            randomFile.seek( length );
        } catch (IOException ex) {
            // some kind of file error
            System.out.println( "File seek error: " + ex.getMessage());
        }
        return length;
    }
    
    
    
    
    
    

    
    /**
     * getRecord
     *
     * @param buffer byte[]
     * @param Rec record number object
     */
    public boolean getRecord( byte[] buffer, Rec rec ){
    	return getRecord( buffer, rec.getRecInt());
    }
    /**
     * getRecord
     *
     * @param buffer byte[]
     * @param int record number
     */
    public boolean getRecord( byte[] buffer, int rec ) {

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	
        // seek to the right spot in the file
        try {
            randomFile.seek((rec - 1) * recLength);
        } catch (IOException ex1) {
            // some kind of file I/O error
            System.out.println( "File seek error: " + ex1.getMessage());
        }

        // read from file into the data buffer
        try {
            randomFile.read(buffer, (int) 0, recLength);
        } catch (IOException ex) {
            // some kind of file read error
            System.out.println( "File read error: " + ex.getMessage());
        }

        return ( true );
    }
    
    
    
    
    
    

    /**
     * putRecord
     *
     * @param buffer byte[]
     * @param randomRecord RandomRecord
     */
    public boolean putRecord(byte[] buffer, Rec rec ) {
    	return putRecord( buffer, rec.getRecInt());
    }
    
    /**
     * putRecord
     *
     * @param buffer byte[]
     * @param randomRecord RandomRecord
     */
    public boolean putRecord(byte[] buffer, int rec ) {

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	
        // seek to the right spot in the file
        try {
            randomFile.seek((rec - 1) * recLength);
        } catch (IOException ex1) {
            // some kind of file I/O error
            System.out.println( "File seek error: " + ex1.getMessage());
        }

        // read the record into the buffer
        try {
            randomFile.write(buffer, (int) 0, recLength);
        } catch (IOException ex) {
            //some kind of file write error
            System.out.println( "File write error: " + ex.getMessage());
        }
        return ( true );
    }

    
    
    
    
    
    /**
     * readBytes
     *
     * @param buffer byte[]
     * @param int record number
     */
    public boolean readBytes( byte[] buffer, int rec, int len ) {

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	
        // seek to the right spot in the file
        try {
            randomFile.seek( rec );
        } catch (IOException ex1) {
            // some kind of file I/O error
            System.out.println( "File seek error: " + ex1.getMessage());
        }

        // read from file into the data buffer
        try {
            randomFile.read(buffer, (int) 0, len );
        } catch (IOException ex) {
            // some kind of file read error
            System.out.println( "File read error: " + ex.getMessage());
        }

        return ( true );
    }
    
    /**
     * writeBytes
     *
     * @param buffer byte[]
     * @param randomRecord RandomRecord
     */
    public boolean writeBytes( byte[] buffer, int rec, int len ) {

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	
        // seek to the right spot in the file
        try {
            randomFile.seek( rec );
        } catch (IOException ex1) {
            // some kind of file I/O error
            System.out.println( "File seek error: " + ex1.getMessage());
        }

        // write the bytes to the file
        try {
            randomFile.write( buffer, (int) 0, len );
        } catch (IOException ex) {
            //some kind of file write error
            System.out.println( "File write error: " + ex.getMessage());
        }
        return ( true );
    }

    
    
    
    
    

    
    
    
    

    
    /**
     * setFileLength
     *
     * @param newLength long
     */
    public boolean setFileLength(long newLength) {

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	
    	try {
            randomFile.setLength(newLength);
        } catch (IOException ex) {
            // some kind of file I/O error
            System.out.println( "File setLength error: " + ex.getMessage());
        }
        return ( true );
    }

    /**
     * setFileLength
     *
     * @param recordLength RandomRecord
     */
    public boolean setFileLength(int maxrec) {
        long newLength = maxrec * this.recLength;

    	// make sure the file is open
    	if ( randomFile == null ) return ( false );
    	

        try {
            randomFile.setLength(newLength);
        } catch (IOException ex) {
            //some kind of file I/O error
            System.out.println( "File setLength error: " + ex.getMessage());
        }
        return ( true );
    }
    
    
    
    
    
    
    

    /**
     * getMaxRecord
     */
    public int getMaxRecord() {
        int maxRec = 0;     // current palmed recs are 32 bits (C longs)


    	// make sure the file is open
    	if ( randomFile == null ) return ( 0 );
    	

        // seek to the beginning of the file
        try {
            randomFile.seek( 0 );
        } catch (IOException ex1) {
                // some kind of file I/O error
                System.out.println( "File seek error: " + ex1.getMessage());
        }

    // read the record into the buffer
        try {

            maxRec = randomFile.readUnsignedByte();
            maxRec = maxRec | (randomFile.readUnsignedByte() << 8 );
            maxRec = maxRec | (randomFile.readUnsignedByte() << 16 );
            maxRec = maxRec | (randomFile.readUnsignedByte() << 24 );

        } catch (IOException ex) {
                //some kind of file write error
                System.out.println( "File read error: " + ex.getMessage());
        }

        return ( maxRec );
    }
    
    
    
    
    /**
     * allocateRecord() - Allocates a record to the open file.
     * 
     * @return Rec
     */
    public Rec allocateRecord(){
    	
    	Rec rec = null;		// record number to return
    	int	tmprec;			// temp int record number
    	
    	// make sure the file is open
    	if ( randomFile == null ) return ( null );
    	
    	
    	//TODO - mxq locking here
    	
    	byte[] buffer = new byte[this.recLength];
    	this.getRecord( buffer, new Rec( 1 ));
    	
    	int maxrec = StructHelpers.getInt( buffer, 0 );
		System.out.println( "allocate - maxrec=" + maxrec );
    	
    	for ( int i = 1; i < (( this.recLength / 4 ) - 1 ); ++ i ){
    		
    		tmprec = StructHelpers.getInt(buffer, i * 4 );
    		
    		if ( tmprec > 1 ){  // found one to re-allocate
 
    			// remove this record from list
    			StructHelpers.setInt( 0, buffer, i * 4 );

    			// error condition when re-alloc list has bad data
    			if ( tmprec > maxrec ) continue;

    			// set record to return
    			rec = new Rec( tmprec );
    			System.out.println( "found empty record to re-alloc rec=" + tmprec );
    			break;
    		}
    	}
    	
    	
    	// if we didn't find an opening, extend the file - updating the maxrec
    	if ( rec == null ){
    		++maxrec;
    		rec = new Rec( maxrec );
    		StructHelpers.setInt( maxrec, buffer, 0 );
			System.out.println( "extended maxrec rec=" + maxrec );
    	}
    	
    	// write out the record
    	this.putRecord( buffer, new Rec( 1 ));
    	
    	//TODO unlock mxq
    	
    	return rec;
    }
    
    
    /**
     * init()
     * 
     * Initialize a new file's first record to have the maxlength=1 and blank empty list.
     * 
     * @return
     */
    public boolean init(){
    	// make 32 bytes the minimum initialized file length
    	int savReclen = recLength;
    	if ( recLength < 32 ) recLength = 32;
    	byte[] dataStruct = new byte[recLength];
		StructHelpers.setInt( 1, dataStruct, 0 );
		boolean status = this.putRecord( dataStruct, 1 );
		recLength = savReclen;
		return status;
    }

    
    

    public String getFileName() {
        return fileName;
    }

    public String getPathName() {
        return pathName;
    }


    
    
    
    
    
    
    
    /**
     * readRec()
     * 
     * This method opens a file, reads a record, closes the file, and returns.
     * 
     * 
     */
    public static boolean readRec( Rec rec, byte[] data, String path, String fname, int rlen ){
    	
    	RandomFile file = RandomFile.open( path, fname, rlen, Mode.READONLY );
    	if ( file == null ) return false;  	
    	boolean ret = file.getRecord( data, rec );
    	file.close();
    	return ret;
    }    
    
    
    /**
     * writeRec()
     * 
     * This method opens a file, writes a record, closes the file, and returns.
     * 
     * Return is a null Rec on error.
     * 
     * @return Rec
     */
    public static Rec writeRec( Rec rec, byte[] data, String path, String fname, int rlen ){
    	
    	RandomFile file = RandomFile.open( path, fname, rlen, Mode.READWRITE );
    	if ( file == null ) return null;
    	if (( rec == null ) || ( rec.getRec() == 0 )) rec = file.allocateRecord();
    		
    	file.putRecord( data, rec );
    	file.close();
    	return rec;
    }
    
    
    
    
    
    
    
    
    /**
     * readBytes()
     * 
     * This method opens a file, reads a specified number of bytes at the specified offset, closes the file, and returns.
     * 
     * 
     */
    public static boolean readBytes( Rec rec, int len, byte[] data, String path, String fname ){
    	
    	RandomFile file = RandomFile.open( path, fname, 1, Mode.READONLY );
    	if ( file == null ) return false; 
    	if (( rec == null ) || ( rec.getRec() < 2 )) return false;
    	
    	boolean ret = file.readBytes( data, rec.getRec(), len );
    	file.close();
    	return ret;
    }
    
    /**
     * writeBytes()
     * 
     * This method opens a file, writes a specified number of bytes to a specified offset, closes the file, and returns.
     * 
     * Return is a null Rec on error.
     * 
     * @return Rec
     */
    public static Rec writeBytes( Rec rec, int len, byte[] data, String path, String fname ){
    	
    	RandomFile file = RandomFile.open( path, fname, 1, Mode.READWRITE );
    	if ( file == null ) return null;
    	
    	// append to file if needed
    	if (( rec == null ) || ( rec.getRec() < 2 )){
    		
    		// TODO - need to handle this
    		
    		//rec = file.allocateRecord();
    		rec = new Rec( 2 );
    	}
    		
    	//file.writeBytes( data, rec.getRec(), len );
    	file.close();
    	return rec;
    }
    
    

    
    
    


}


/**/

