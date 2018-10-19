package palmed;

import java.io.File;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import usrlib.Date;
import usrlib.ImageHelper;
import usrlib.Rec;
import usrlib.Reca;

public class PtPortalViewWinController extends GenericForwardComposer {

	private Rec	ptRec = null;

	
	// Display fields at top of chart (ALL AUTOWIRED)
	private Window ptPortalViewWin;		// autowired
	private Label ptname;			// autowired - patient name
	private Label ptnum;			// autowired - patient number
	private Label ptssn;			// autowired - patient SSN
	private Label ptrec;			// autowired - pt record number
	private Label ptsex;			// autowired - pt sex
	private Label ptdob;			// autowired - pt birthdate
	private Label ptage;			// autowired - pt age
	
	private Image ptImage;			// autowired - pt's picture
	
	private Label address1;
	private Label address2;
	private Label address3;
	private Label home_ph;
	private Label work_ph;
	
	private Listbox meds;			// autowired - medications listbox
	private Listbox pars;			// autowired - pars listbox
	private Listbox problems;		// autowired - problems listbox
	private Listbox labs;
	
	
	
	

	private Component parent;
	
	private MedPt	medPt;			// patient's medical record
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println( "in doAfterCompose()" );
		

		
		// Get arguments from map
		Execution exec=Executions.getCurrent();

		if ( exec != null ){
			Map<String, Rec> myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		
		System.out.println( "got ptRec from myMap" );
		if ( ptRec == null ) System.out.println( "ptRec == null" );
		else System.out.println( "ptRec=" + ptRec.getRec());
		
		

		
		// DEBUG
		// Normally would not get here without args and ptRec set 
		//   -- BUT I needed to be able to for easy testing
		//TODO - take out this hard code ptRec
		if (( ptRec == null ) || ( ptRec.getRec() == 0 )) ptRec = new Rec( 7072 );
		System.out.println( "ptRec=" + ptRec.getRec());
		
		// Read patient info DIRPT and INFOPT
		DirPt dirPt = new DirPt( ptRec );
		
	
		// Get some patient values and set labels
		ptrec.setValue( "" + ptRec.getRec());
		ptPortalViewWin.setTitle( dirPt.getName().getPrintableNameLFM());
		
		
		ptssn.setValue( dirPt.getSSN());
		ptnum.setValue( dirPt.getPtNumber());
		ptsex.setValue( dirPt.getSex().getAbbr());
		ptdob.setValue( dirPt.getBirthdate().getPrintable( 9 ));
		ptage.setValue( Date.getPrintableAge( dirPt.getBirthdate()));
		
		// patient address
		{
		String s2, s3;
		address1.setValue( dirPt.getAddress().getPrintableAddress(1));
		address2.setValue( s2 = dirPt.getAddress().getPrintableAddress(2));
		s3 = dirPt.getAddress().getPrintableAddress(3);
		if ( s2.trim().length() == 0 ){
			address2.setValue( s3 );
			address3.setValue( "" );
		} else {
			address3.setValue( s3 );
		}
	
		work_ph.setValue( dirPt.getAddress().getWork_ph());
		home_ph.setValue( dirPt.getAddress().getHome_ph());
		}

		
		
		System.out.println( "chart created ptrec=" + ptRec.getRec());
		//System.out.println( "name=" + ptname.getValue());

	
		
		
		
		// Image
		{
			String filename = String.format( "f%07d.bmp", ptRec.getRec());
			if ( new File( Pm.getMedPath() + "/faces", filename ).exists()){
				ptImage.setContent( ImageHelper.showImage( Pm.getMedPath() + "/faces/" + filename, 125, 150 ));
			}
		}
		
		
		
		
		// get patient's medical record
		medPt = new MedPt( dirPt.getMedRec());
		

		//ERxConfig eRx = new ERxConfig();
		//eRx.read();
		
		
		// populate Med list
		for ( Reca reca = medPt.getMedsReca(); reca.getRec() != 0; ){
			
			Cmed cmed = new Cmed( reca );
			if (( cmed.getStatus() == Cmed.Status.CURRENT ) || ! cmed.getStopDate().isValid()){
				meds.appendItem( cmed.toString(), "" );	
			}
			reca = cmed.getLLHdr().getLast();
		}

		// populate Problem list
		for ( Reca reca = medPt.getProbReca(); reca.getRec() != 0; ){
			
			Prob prob = new Prob( reca );
			if ( prob.getStatus() == 'C' ){		// only active 'Current' problems
				problems.appendItem( prob.getProbDesc(), "" );	
			}
			reca = prob.getLLHdr().getLast();
		}
		
		// populate PARs and allergies
		for ( Reca reca = medPt.getParsReca(); reca.getRec() != 0; ){
			
			Par par = new Par( reca );
			if ( par.getStatus() == Par.Status.CURRENT ){
				pars.appendItem( par.toString(), "" );	
			}
			reca = par.getLLHdr().getLast();
		}
		
		for ( Reca reca = medPt.getLabResultReca(); Reca.isValid( reca ); ){
			
			LabResult lab = new LabResult( reca );
			if ( lab.getStatus() == LabResult.Status.ACTIVE ){
				Rec orec = lab.getLabObsRec();
				if ( Rec.isValid( orec )){
					Rec batRec = lab.getLabBatRec();
					String batStr = "";
					if ( Rec.isValid( batRec )){
						LabBat bat = new LabBat( batRec );
						batStr = bat.getDesc();
					}
					LabObsTbl obs = new LabObsTbl( orec );
					String s = String.format( "%s %15.15s %15.15s %s %s", lab.getDate().getPrintable(9), batStr, obs.getDesc(), lab.getResult(), obs.getCodedUnits().getLabel());
					labs.appendItem( s , "" );
				}
			}
			reca = lab.getLLHdr().getLast();
		}

		
		// log the access
		AuditLogger.recordEntry( AuditLog.Action.PT_PORTAL, ptRec, null, null, "IP: " + Executions.getCurrent().getRemoteAddr() );
		
		
		
		return;
	}
	
	
	
	
	public void onClick$btnClose(){		
		ptPortalViewWin.detach();
		return;
	}
	
	public void onClick$btnPrint(){
		Clients.print();
	}
	
	public void onClick$btnCCR(){
		PtCCR.exportCCRtoPatient( ptRec );
	}
	
	

	

}
