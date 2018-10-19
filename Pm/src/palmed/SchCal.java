package palmed;

import usrlib.RandomFile;
import usrlib.Rec;


/*  a schedule slot  */

//typedef struct {
//
//	RECSH   aprec;                  /*  appointment record  */
//	RECSH   resource[4];            /*  resource scheduled  */
//	RECSH   provider[2];            /*  provider scheduled  */
//	unsigned short flags;           /*  flags  */
//
//	} SCHSLOT;
//
//      sizecheck( 16, sizeof( SCHSLOT ), "SCHSLOT" );




/*  month's schedule structure  */

//typedef struct {
//
//	SCHSLOT slot[3];                /*  3 slots per time  */
//	RECSH   extend;                 /*  extension record  */
//
//	} SCHTM;
//
//      sizecheck( 50, sizeof( SCHTM ), "SCHTM" );


//typedef struct {
//
//	SCHTM   schtm[SCHTMHR];                 /*  12 time lines per hour  */
//
//	} SCHHR;
//
//      sizecheck( sizeof( SCHTM ) * SCHTMHR, sizeof( SCHHR ), "SCHHR" );


//typedef struct {
//
//	SCHHR   hour[24];               /*  24 hours per day  */
//
//	} SCHDY;
//
//      sizecheck( 24 * sizeof( SCHHR ), sizeof( SCHDY ), "SCHDY" );





public class SchCal {
	
	private static final String filename = "ca%02d%02d%02d.sch";
	private static final int recordLength = 16;


	// Basic calendar structure
    public static int sz_SchSlot = 16;
	public static int sz_SchTm = 3 * sz_SchSlot + 2;
	public static int sz_SchHr = 12 * sz_SchTm;
	public static int sz_SchDay = 24 * sz_SchHr;

	
	private Rec rec = null;
	private Rec maxrec = null;
	private RandomFile file = null;
	
	
    public byte[] dataStruct;
    
    
    class SchDay {
    	
    }
    class SchHr {
    	
    }
    class SchTm {
    	private byte[] dataStruct = new byte[sz_SchTm];
    	
    	public SchTm read( int book, int month, int day, int year, int hour, int min ){
    		String fname = String.format( filename, book, month, year % 100 );
    		RandomFile.readRec( getTmRec( day, hour, min ), dataStruct, Pm.getSchPath(), fname, recordLength );
    		return this;
    	}
    	
    	public SchSlot getSlot( int num ){
    		if (( num < 0 ) || ( num > 2 )) SystemHelpers.seriousError( "SchCal.getSlot() array bounds excceded: " + num );
    		byte[] newData = new byte[sz_SchSlot]; 
    		System.arraycopy(dataStruct, num * 16, newData, 0, sz_SchSlot );
    		SchSlot slot = new SchSlot();
    		slot.setDataStruct( newData );
			return slot;
    	}
    	
    }
    
    
    public static Rec getTmRec( int day, int hour, int min ){    	
    	return new Rec( ( day - 1 ) * sz_SchDay + (hour * sz_SchHr ) + min / 5 * sz_SchTm ); 
    }
	
	
	public SchCal(){
		//SchSlot slot = new SchTm().read(0, 0, 0, 0, 0, 0).getSlot(0);
		
	}

	
	
	
	/*	
	
	public static SchSlot readSlot( int book, int month, int day, int year, int hour, int min, int slotnum ){
		
		SchSlot slot = new SchSlot();
		
		String fname = String.format( filename, book, month, year % 100 );
		//RandomFile.readRec(rec, slot.getDataStruct(), Pm.getSchPath(), fname, recordLength );
		
		
		return slot;
	}
	
	public static SchCal.SchTm readTm( int book, int month, int day, int year, int hour, int min, int slotnum ){
		
		SchSlot slot = new SchSlot();
		
		String fname = String.format( filename, book, month, year % 100 );
		//RandomFile.readRec(rec, slot.getDataStruct(), Pm.getSchPath(), fname, recordLength );
		
		
		return slot;
	}
*/	
	
 
}
