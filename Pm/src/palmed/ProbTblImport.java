package palmed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Tabpanel;

import usrlib.DialogHelpers;

public class ProbTblImport {


	
	public static int importSnomed( BufferedReader reader ){
	
	
		String cid;
		String fsn;
		String status;
		String cui;
		String occurrence;
		String usageNum;
		String firstInSubset;
		String isRetiredFromSubset;
		String lastInSubset;
		String replacedBySnomedCID;
		
		String line;
		
		int loaded = 0;
	
	
		
		// discard first (header) line
		try {
			line = reader.readLine();
		} catch (IOException e) {
			DialogHelpers.Messagebox( "Error reading first line of input file." );
			return 0;			
		}
		
		if ( line.indexOf( "SNOMED_CID" ) < 0 ){			
			DialogHelpers.Messagebox( "Not a valid SNOMED core concepts file." );
			return 0;			
		}
		
		
		

		
		try {
			while (( line = reader.readLine()) != null ){
			
				String[] tokens = line.split( "\\|", 10 );
/*				if ( tokens.length != 8 ){
					System.out.println( "invalid line, tokens=" + tokens.length );
					System.out.println( "line=" + line );
					continue;
				}
*/
				
				cid = tokens[0];
				fsn = tokens[1];
				status = tokens[2];
				cui = tokens[3];
				occurrence = tokens[4];
				usageNum = tokens[5];
				firstInSubset = tokens[6];
				isRetiredFromSubset = tokens[7];
				lastInSubset = tokens[8];
				replacedBySnomedCID = tokens[9];
				
				// should we include this one?
				if ( ! status.equals( "Current" )) continue;
				if ( ! isRetiredFromSubset.equals( "False" )) continue;
				
				// make sure important fileds are not null
				if (( cid.trim().length() < 6 )
					|| ( fsn.trim().length() < 6 )){
					System.out.println( "Invalid line==>" + line );
					continue;
				}
				
				
				ProbTbl prob = new ProbTbl();
				prob.setAbbr( cid );
				prob.setDesc( fsn );
				prob.setSNOMED( cid );
				prob.setStatus( ProbTbl.Status.ACTIVE );
				prob.writeNew();
				++loaded;
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println( "Number of concepts loaded = " + loaded );
	
		return loaded;
	}
	
	
	
	
	
}
