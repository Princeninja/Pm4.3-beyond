package palmed;

import java.io.*;
import java.util.*;

import usrlib.*;
import usrlib.Date;


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
public class PtFinder{
	
    public PtFinder(){
    }

    /**
     * reset
     */
    public void reset() {
        this.ptList = null;
    }

    
    
    
    
    

    /**
     * doSearch() - Search for a patient by name.
     *
     * @param Name srcName - patient name to search for
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearch( Name srcName ) { return doSearchName( srcName, 0 ); }

    

    /**
     * doSearchName() - Search for a patient by name.
     *
     * This method is called to actually do the search.
     * @param Name srcName - patient name to search for
     * @param int type - type of search ( 0-reg, 1-anchored, 2-unanchored
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchName( Name srcName, int type ) {

        RandomFile file;
        RandomFile file1;
        DirPt dirpt = new DirPt();
        int rec = 0;
        int maxrec = 0;
        Name dbName;      // name from dirpt
        int fnd = 0;          // number found

        // set > max type to default to normal search
        if ( type > 3 ) type = 0;

        // open file
        file = new RandomFile( Pm.getMedPath(), "dir-pt.med", DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file1 = new RandomFile( Pm.getMedPath(), "info-pt.med", DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );

        // get max record
        maxrec = file.getMaxRecord();
        //System.out.println( "maxrec = " + maxrec.getLong());
        //System.out.println( "searching for " + srcName.getPrintableNameLFM());

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( dirpt.dataStruct, rec );
            file1.getRecord( dirpt.infoDataStruct, rec );
            if ( ! dirpt.isValid()) continue;

            dbName = dirpt.getName();
            //System.out.println( "rec = " + rec.getLong() + "     " + dbName.getPrintableNameLFM());

            if ((( type == 0 ) && srcName.compare( dbName ))
            	|| (( type == 1 ) && srcName.compareAnchoredSubstring(dbName))
            	|| (( type == 2 ) && srcName.compareUnAnchoredSubstring(dbName))
            	|| (( type == 3 ) && srcName.compareSoundsLike( dbName ))){
            	
                //System.out.println( "found a match, rec=" + rec.getLong() + ", " + dbName.getPrintableNameLFM() + "DOB: " + dirpt.getBirthdate().getPrintable());

                // create a patient list entry
                PtFoundList ptFnd = new PtFoundList();
                ptFnd.setPtRec( new Rec( rec ));
                ptFnd.setName( dbName );
                ptFnd.setBirthdate( dirpt.getBirthdate());
                ptFnd.setSSN( dirpt.getSSN());

                // allocate room for pt listings with first find only
                if ( fnd == 0 ) this.ptList = new Vector<PtFoundList>( 10, 10 );

                // add this patient to list
                ptList.addElement( ptFnd );
                ++fnd;
            }


        }
        //System.out.println( "loop finished, number found = " + fnd );

        // close file
        file.close();
        file1.close();

        return ( fnd );
    }

    
  
    
    /**
     * doSearchName() - Search for a patient by name AND birthdate.
     * Both must match.
     *
     * This method is called to actually do the search.
     * @param Name srcName - patient name to search for
     * @param int type - type of search ( 0-reg, 1-anchored, 2-unanchored
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchName( Name srcName, Date bdate ) {

        RandomFile file;
        RandomFile file1;
        DirPt dirpt = new DirPt();
        int rec = 0;
        int maxrec = 0;
        Name dbName;      // name from dirpt
        int fnd = 0;          // number found


        // open file
        file = new RandomFile( Pm.getMedPath(), "dir-pt.med", DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file1 = new RandomFile( Pm.getMedPath(), "info-pt.med", DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );

        // get max record
        maxrec = file.getMaxRecord();
 
        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( dirpt.dataStruct, rec );
            file1.getRecord( dirpt.infoDataStruct, rec );
            if ( ! dirpt.isValid()) continue;

            dbName = dirpt.getName();
 
            if ( srcName.compare( dbName ) && ( bdate.equals( dirpt.getBirthdate()))){
            	
                // create a patient list entry
                PtFoundList ptFnd = new PtFoundList();
                ptFnd.setPtRec( new Rec( rec ));
                ptFnd.setName( dbName );
                ptFnd.setBirthdate( dirpt.getBirthdate());
                ptFnd.setSSN( dirpt.getSSN());

                // allocate room for pt listings with first find only
                if ( fnd == 0 ) this.ptList = new Vector<PtFoundList>( 10, 10 );

                // add this patient to list
                ptList.addElement( ptFnd );
                ++fnd;
            }


        }

        // close file
        file.close();
        file1.close();

        return ( fnd );
    }

    

    /**
     * doSearchSSN() - Search for a patient by SSN
     *
     * @param String SSN
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchSSN( String srcSSN ) {

        RandomFile file;
        RandomFile file1;
        DirPt dirpt = new DirPt();
        int rec = 0;
        int maxrec = 0;
        String dbSSN;		// SSN from dirpt
        int fnd = 0;		// number found
        
        
        
        // make sure srcSSN doesn't contain any dashes and is digits only
        


        // open file
        file = new RandomFile( Pm.getMedPath(), "dir-pt.med", DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file1 = new RandomFile( Pm.getMedPath(), "info-pt.med", DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );

        // get max record
        maxrec = file.getMaxRecord();

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( dirpt.dataStruct, rec );
            file1.getRecord( dirpt.infoDataStruct, rec );
            if ( ! dirpt.isValid()) continue;

            dbSSN = dirpt.getSSN( false );

            if ( srcSSN.equals( dbSSN )){

                // create a patient list entry
                PtFoundList ptFnd = new PtFoundList();
                ptFnd.setPtRec( new Rec( rec ));
                ptFnd.setName( dirpt.getName());
                ptFnd.setBirthdate( dirpt.getBirthdate());
                ptFnd.setSSN( dirpt.getSSN());

                // allocate room for pt listings with first find only
                if ( fnd == 0 ) this.ptList = new Vector<PtFoundList>( 10, 10 );

                // add this patient to list
                ptList.addElement( ptFnd );
                ++fnd;
            }


        }
        //System.out.println( "loop finished, number found = " + fnd );

        // close file
        file.close();
        file1.close();

        return ( fnd );
    }

    
    
    /**
     * doSearchNum() - Search for a patient by Patient Num
     *
     * @param String Num
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchNum( String srcNum ) { return doSearchNum( srcNum, 0 ); }

    /**
     * doSearchNum() - Search for a patient by Patient Num
     *
     * @param String Num
     * @param int type - type of search (0-normal, 1-anchored, 2-unanchored)
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchNum( String srcNum, int type ) {

        RandomFile file;
        RandomFile file1;
        DirPt dirpt = new DirPt();
        int rec = 0;
        int maxrec = 0;
        int fnd = 0;		// number found
        
        // temp - no sounds like
        if ( type > 2 ) type = 0;
        
        
        // open file
        file = new RandomFile( Pm.getMedPath(), "dir-pt.med", DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file1 = new RandomFile( Pm.getMedPath(), "info-pt.med", DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );

        // get max record
        maxrec = file.getMaxRecord();

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( dirpt.dataStruct, rec );
            file1.getRecord( dirpt.infoDataStruct, rec );
            if ( ! dirpt.isValid()) continue;
            
            
            // check srcNum against the various patient numbers
            
            if ((( type == 0 ) && ( dirpt.getPtNumber( 1 ).equals( srcNum ) || dirpt.getPtNumber( 2 ).equals( srcNum ) || dirpt.getPtNumber( 3 ).equals( srcNum )))
            	|| (( type == 1 ) && ( dirpt.getPtNumber( 1 ).startsWith(srcNum) || dirpt.getPtNumber(2).startsWith(srcNum) || dirpt.getPtNumber(3).startsWith(srcNum)))
            	|| (( type == 2 ) && (( dirpt.getPtNumber( 1 ).indexOf(srcNum) > 1 ) || ( dirpt.getPtNumber(2).indexOf(srcNum) >= 0 ) || ( dirpt.getPtNumber(3).indexOf(srcNum) >= 0 )))            	
            	){
 
                // create a patient list entry
                PtFoundList ptFnd = new PtFoundList();
                ptFnd.setPtRec( new Rec( rec ));
                ptFnd.setName( dirpt.getName());
                ptFnd.setBirthdate( dirpt.getBirthdate());
                ptFnd.setSSN( dirpt.getSSN());

                // allocate room for pt listings with first find only
                if ( fnd == 0 ) this.ptList = new Vector<PtFoundList>( 10, 10 );

                // add this patient to list
                ptList.addElement( ptFnd );
                ++fnd;
            }
        }

        // close file
        file.close();
        file1.close();

        return ( fnd );
    }



    
    /**
     * doSearchDOB() - Search for a patient by Birthdate
     *
     * @param usrlib.Date DOB
     * @return int ret    // Returns the number of entries found.
     *
     * @todo  Implement indexed search (did brute force for now)
     */
    public int doSearchDOB( usrlib.Date srcDOB ) {

        RandomFile file;
        RandomFile file1;
        DirPt dirpt = new DirPt();
        int rec = 0;
        int maxrec = 0;
        int fnd = 0;		// number found
        
        
        
        // make sure date is valid
        


        // open file
        file = new RandomFile( Pm.getMedPath(), "dir-pt.med", DirPt.getRecordLength(), RandomFile.Mode.READONLY );
        file1 = new RandomFile( Pm.getMedPath(), "info-pt.med", DirPt.getInfoRecordLength(), RandomFile.Mode.READONLY );

        // get max record
        maxrec = file.getMaxRecord();

        // loop through all pt records
        for ( rec = 2; rec <= maxrec; ++rec ){

            file.getRecord( dirpt.dataStruct, rec );
            file1.getRecord( dirpt.infoDataStruct, rec );
            if ( ! dirpt.isValid()) continue;
            
            
            // check srcDOB against the patients in database
            
            if ( dirpt.getBirthdate().equals( srcDOB )){
 
                // create a patient list entry
                PtFoundList ptFnd = new PtFoundList();
                ptFnd.setPtRec( new Rec( rec ));
                ptFnd.setName( dirpt.getName());
                ptFnd.setBirthdate( dirpt.getBirthdate());
                ptFnd.setSSN( dirpt.getSSN());

                // allocate room for pt listings with first find only
                if ( fnd == 0 ) this.ptList = new Vector<PtFoundList>( 10, 10 );

                // add this patient to list
                ptList.addElement( ptFnd );
                ++fnd;
            }
        }

        // close file
        file.close();
        file1.close();

        return ( fnd );
    }


    

    /**
     * getPtList()
     */
    public Vector<PtFoundList> getPtList() {
        return ( this.ptList );
    }

    private Vector<PtFoundList> ptList = null;    // list of patients found



}

/**/

