package palmed;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import palmed.CQMWinController.Measure;

import usrlib.Date;
import usrlib.EditHelpers;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.ZkTools;

public class AMCWinController extends GenericForwardComposer {

	private Window amcWin;			// autowired
	Rows rows;
	Listbox lboxYear;
	Listbox lboxPeriod;
	
	
	int startRec = 2; //16000;
	
	
	
	
	class Measure {
		String name = "";
		String desc = "";
		int num = 0;
		int denom = 0;
		int exc = 0;
		int target = 0;
		
		Measure( String s, String desc, int target ){ name = s; this.desc = desc; this.target = target; }
		
		void incrNum(){ ++num; }
		void incrDenom(){ ++denom; }
		void incrExc(){ ++exc; };
		void setName( String s ){ name = s; }
		void setDesc( String s ){ desc = s; }
		
		int getNum(){ return num; }
		int getDenom(){ return denom; }
		int getExc(){ return exc; }
		int getTarget(){ return target; }
		String getName(){ return name; }
		String getDesc(){ return desc; }
		
	}
	
	

	

	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// Get arguments from map
		//Execution exec = Executions.getCurrent();

		//if ( exec != null ){
		//	Map myMap = exec.getArg();
		//	if ( myMap != null ){
		//		try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
		//	}
		//}		
		
		
		// populate listbox
		for ( int year = Date.today().getYear(); year > 2009; --year ){
			ZkTools.appendToListbox( lboxYear, "" + year, new Integer( year ));
		}
		
		//refreshList();
		
		return;
	}
	
	
	
	
	
	
	
	
	
	
	
	// Open dialog to edit existing problem table entry
	
	public void onClick$calculate( Event ev ){

		palmed.Sex sex = null;
		int age = 0;
		boolean greater = true;
		
		int allPts = 0;
		int problems = 0;
		int meds = 0;
		int allergies = 0;
		int vitals = 0;
		int labs = 0;
		int smoke = 0;
		
		
		
		Date startDate = null;
		Date endDate = null;


		// empty list
		List list = rows.getChildren();
		for ( int i = list.size(); i > 0; )
			((Component)( list).get( --i )).detach();
		
		
		// get selections from year and period listboxes
		int year = 0;
		if ( lboxYear.getSelectedCount() > 0 ){
			year = (Integer) ZkTools.getListboxSelectionValue( lboxYear );
		}		
		if (( year < 2009 ) || ( year > Date.today().getYear())) year = Date.today().getYear();
		

		String period = (String) ZkTools.getListboxSelectionValue( lboxPeriod );
		if ( period == null ) period="A";
		
		if ( period.equals( "Q1" )){
			startDate = new Date( 1, 1, year );
			endDate = new Date( 3, 31, year );

		} else if ( period.equals( "Q2" )){
			startDate = new Date( 4, 1, year );
			endDate = new Date( 6, 30, year );			

		} else if ( period.equals( "Q3" )){
			startDate = new Date( 7, 1, year );
			endDate = new Date( 9, 30, year );			

		} else if ( period.equals( "Q4" )){
			startDate = new Date( 10, 1, year );
			endDate = new Date( 12, 31, year );			

		} else if ( period.equals( "S1" )){
			startDate = new Date( 1, 1, year );
			endDate = new Date( 6, 30, year );			

		} else if ( period.equals( "S2" )){
			startDate = new Date( 7, 1, year );
			endDate = new Date( 12, 31, year );			

		} else if ( period.equals( "A" )){
			startDate = new Date( 1, 1, year );
			endDate = new Date( 12, 31, year );			
		}
		
		
		
		
		
		
		
		
		Measure mProb = new Measure( "Problems", "Maintain up to date problem list.", 80 );
		Measure mMeds = new Measure( "Meds", "Maintain an active medication list.", 80 );
		Measure mAllergy = new Measure( "Allergy", "Maintain an active medication allergy list.", 80 );
		Measure mDemo = new Measure( "Demographics", "Record patient demographics.", 50 );
		Measure mPtEd = new Measure( "Pt Education", "Provide patient education resources.", 10 );
		Measure mTimely = new Measure( "Timely Access", "Provide timely access.", 10 );
		Measure mCPOE = new Measure( "CPOE", "Use CPOE for medication orders.", 30 );
		Measure mERX = new Measure( "Electronic RX", "Transmit prescriptions electronically.", 40 );
		Measure mVitals = new Measure( "Vital Signs", "Record and chart vital signs.", 50 );
		Measure mSmoking = new Measure( "Smoking", "Record smoking status.", 50 );
		//Measure mAD = new Measure( "Advanced Directives", "Record advanced directives.", 50 );
		Measure mLabs = new Measure( "Lab", "Incorporate Lab Results.", 40 );
		Measure mCCR = new Measure( "Electronic Copy", "Provide electronic copy of health record.", 50 );
		//Measure mDischarge = new Measure( "Discharge", "Provide electronic copy of discharge instructions.", 50 );
		Measure mVisitSummary = new Measure( "Visit Summary", "Provide visit summary within 3 days.", 50 );
		Measure mReminders = new Measure( "Reminders", "Send patient reminders.", 20 );
		Measure mMedRec = new Measure( "Med Reconciliation", "Medication Reconciliation.", 50 );
		Measure mTransferOutSummary = new Measure( "Transfer Summary", "Provide summary of care on transfer OUT.", 50 );

		
		
		
		
		
		
		
		
		doProb( mProb, startDate, endDate );
		doMeds( mMeds, startDate, endDate );
		doAllergy( mAllergy, startDate, endDate );
		doDemo( mDemo, startDate, endDate );
		doPtEd( mPtEd, startDate, endDate );
		doTimely( mTimely, startDate, endDate );
		doCPOE( mCPOE, startDate, endDate );
		doERX( mERX, startDate, endDate );
		doVitals( mVitals, startDate, endDate );
		doSmoking( mSmoking, startDate, endDate );
		doLabs( mLabs, startDate, endDate );
		doCCR( mCCR, startDate, endDate );
		doVisitSummary( mVisitSummary, startDate, endDate );
		doReminders( mReminders, startDate, endDate );
		doMedRec( mMedRec, startDate, endDate );
		doTransferOutSummary( mTransferOutSummary, startDate, endDate );

		
		
		
		
		
		
		
		
		// fill out table
		setTableRows( mProb );
		setTableRows( mMeds );
		setTableRows( mAllergy );
		setTableRows( mDemo );
		setTableRows( mPtEd );
		setTableRows( mTimely );
		setTableRows( mCPOE );
		setTableRows( mERX );
		setTableRows( mVitals );
		setTableRows( mSmoking );
		setTableRows( mLabs );
		setTableRows( mCCR );
		setTableRows( mVisitSummary );
		setTableRows( mReminders );
		setTableRows( mMedRec );
		setTableRows( mTransferOutSummary );



		
		
	
		return;
	}
	
	
	
	
	
	
	public void onClick$print( Event ev ){
		Clients.print();
		return;
	}
	
	
	
	void setTableRows( Measure m ){
		
		Row row = new Row();
		row.setParent( rows );
		row.appendChild( new Label( m.getName()));
		row.appendChild( new Label( m.getDesc()));
		row.appendChild( new Label( String.valueOf( m.getNum())));
		row.appendChild( new Label( String.valueOf( m.getDenom())));
		row.appendChild( new Label( fmtPercents( m.getNum(), m.getDenom(), m.getExc() ) + "%" ));
		row.appendChild( new Label( String.valueOf( m.getTarget()) + "%" ));
		return;
	}
	
	
	boolean withinDateRange( Date refDate, Date startDate, Date endDate ){
		return  (( startDate.compare( refDate ) <= 0 ) && ( endDate.compare( refDate ) >= 0 ));

	}
	

	String fmtPercents( int num, int denom, int excl ){
		if (( denom - excl ) <= 0 ) return "";		// no divide by zeroes
		return String.format( "%.1f", ( 100.0 * ((double) num) / ((double)( denom - excl ))));
	}
	
	
	
	
	
	
	
	
	

	boolean hadVisit( MedPt medpt, Date startDate, Date endDate ){

		boolean flgVisit = false;
		
		
		Vitals v = null;
		
		for ( Reca reca = medpt.getVitalsReca(); Reca.isValid( reca ); reca = v.getLLHdr().getLast()){				
			v = new Vitals( reca );				
			if ( withinDateRange( v.getDate(), startDate, endDate )){					
				flgVisit = true;
				break;
			}
		}
		
		
		// if didn't find a visit - maybe older system - look through soap notes
		if ( ! flgVisit ){
			
			SoapNote soap = null;			
			for ( Reca reca = medpt.getSoapReca(); Reca.isValid( reca ); reca = soap.getLLHdr().getLast()){				
				soap = new SoapNote( reca );				
				if ( withinDateRange( soap.getDate(), startDate, endDate )){					
					flgVisit = true;
					break;
				}
			}			
		}
		
		return flgVisit;
	}
	
	
	
	
	
	
	
	void doProb( Measure m, Date startDate, Date endDate ){
	
		System.out.println( m.getName() + "  " + m.getDesc());
		
		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgProb = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());
	
			
			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			
			
			if ( medpt.getNoProbsFlag()){
				flgProb = true;
			} else {
				
				for ( Reca reca = medpt.getProbReca(); Reca.isValid( reca ); ){
					Prob p = new Prob( reca );
						flgProb = true;
						break;						
					//reca = p.getLLHdr().getLast();
				}
			}

			m.incrDenom();
			if (flgProb ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	
	void doMeds( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgMeds = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());
			
			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			
			if ( medpt.getNoMedsFlag()){
				flgMeds = true;
			} else {

				Cmed p = null;
				for ( Reca reca = medpt.getMedsReca(); Reca.isValid( reca ); ){
					p = new Cmed( reca );
						flgMeds = true;
						break;
						
					//reca = p.getLLHdr().getLast();
				}
			}
			
			m.incrDenom();
			if ( flgMeds ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	
	void doAllergy( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgAllergy = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			
			if ( medpt.getNKDAFlag()){
				flgAllergy = true;
			} else {
			
				Par p = null;
				for ( Reca reca = medpt.getParsReca(); Reca.isValid( reca ); ){
					p = new Par( reca );
						flgAllergy = true;
						break;
						
					//reca = p.getLLHdr().getLast();
				}
			}
			
			m.incrDenom();
			if ( flgAllergy ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	
	void doDemo( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgDemo = false;


			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			

			if ( dirPt.getBirthdate().isValid()
					&& (( dirPt.getSex() == Sex.MALE ) || ( dirPt.getSex() == Sex.FEMALE ))
					&& (( dirPt.getRace() != null ) && ( dirPt.getRace() != Race.UNSPECIFIED ))
					&& (( dirPt.getEthnicity() != null ) && ( dirPt.getEthnicity() != Ethnicity.UNSPECIFIED ))
					&& (( dirPt.getLanguage() != null ) && ( dirPt.getLanguage() != Language.UNSPECIFIED ))){
				flgDemo = true;
			}
			
			m.incrDenom();
			if ( flgDemo ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	
	
	
	void doPtEd( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgInt = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			
			Intervention p = null;
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.PATIENT_EDUCATION )
						&& withinDateRange( p.getDate(), startDate, endDate )){					
					flgInt = true;
					break;
				}
			}

			m.incrDenom();
			if ( flgInt ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	void doTimely( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgTimely = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			flgTimely = true;
/*
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); ){
				Intervention p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.PATIENT_EDUCATION )
						&& withinDateRange( p.getDate(), startDate, endDate )){					
					flgInt = true;
					break;
				}
				reca = p.getLLHdr().getLast();
			}
*/
			m.incrDenom();
			if ( flgTimely ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	void doCPOE( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgMed = false;
			boolean flgERx = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			
			Cmed p = null;
			
			for ( Reca reca = medpt.getMedsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Cmed( reca );
				// date range?????
				flgMed = true;
				if (( p.getChecked() == 2 ) || (p.getChecked() == 3 )){	// pulled back from newcrop
					flgERx = true;
					break;
				}
			}

			if ( flgMed ) m.incrDenom();
			if ( flgMed && flgERx ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	
	void doERX( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			MedPt medpt = new MedPt( dirPt.getMedRec());
			Cmed p = null;
			
			for ( Reca reca = medpt.getMedsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Cmed( reca );
				if (( p.getDEAClass() == 0 )){					// unable to send Classed meds electronically for now
					//&& ( Rec.isValid( p.getProviderRec()))){		// one of our provider's prescriptions
																//TODO - check flag not managed by us
				
					// date range?????
					if ( withinDateRange( p.getStartDate(), startDate, endDate )){
						
						m.incrDenom();
						if (( p.getChecked() == 2 ) || ( p.getChecked() == 3 )){
							m.incrNum();
						}					
					}
				}
				
				;
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	
	void doVitals( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgHt = false;
			boolean flgWt = false;
			boolean flgBP = false;
			boolean flgVisit = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());
			Vitals v = null;
			
			for ( Reca reca = medpt.getVitalsReca(); Reca.isValid( reca ); reca = v.getLLHdr().getLast()){				
				v = new Vitals( reca );				
				if ( withinDateRange( v.getDate(), startDate, endDate )){					
					if ( Date.getAgeYears( dirPt.getBirthdate(), v.getDate()) > 1 ) flgVisit = true;
					if ( v.getHeight() > 0 ) flgHt = true;
					if ( v.getWeight() > 0 ) flgWt = true;
					if ( v.getBP().length() > 0 ) flgBP = true;
				}
			}
			
			if ( flgVisit ) m.incrDenom();
			if ( flgVisit && /*TEMP flgHt &&*/ flgWt && flgBP ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	
	
	
	void doSmoking( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgSmoking = false;
			
			if ( Date.getAgeYears( dirPt.getBirthdate(), startDate ) < 13 ) continue;
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			//    if no visit, continue;
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			// is there a new style smoking record?
			if ( Reca.isValid( medpt.getSmokingReca())) flgSmoking = true;
			
			
			
			// or - look for documentation of tobacco use/non-use in problem list
			if ( ! flgSmoking ){
				Prob p = null;
				for ( Reca reca = medpt.getProbReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
					p = new Prob( reca );
					String prob = p.getProbDesc().toLowerCase();
					if (( prob.indexOf( "smok" ) >= 0 ) || ( prob.indexOf( "tobacco" ) >= 0 )){
						flgSmoking = true;
					}
				}
			}
			
			
			// TODO - or could look in old medical history


			// increment numerator and denominator
			m.incrDenom();
			if ( flgSmoking ) m.incrNum();
		}
		
		dirPt.close();
		return;
	}
	
	
	

	void doLabs( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgLab = false;
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			// was there a visit within the date range?
			if ( ! hadVisit( medpt, startDate, endDate )) continue;
			

			
			Orders p = null;
			
			for ( Reca reca = medpt.getOrdersReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				
				// look through lab orders
				p = new Orders( reca );
				if ( ! withinDateRange( p.getDate(), startDate, endDate )) continue;
				
				String desc = p.getDesc().toLowerCase();
				if ( desc.indexOf( "lab" ) >= 0 ){
					
					// is there a similar lab in a similar time period?
					for ( Reca lreca = medpt.getOrdersReca(); Reca.isValid( lreca ); ){
						
						// look through lab orders
						LabResult r = new LabResult( lreca );
						if ( r.getDate().compare( p.getDate()) < 14 ){
							flgLab = true;
							break;
						}
						
						lreca = r.getLLHdr().getLast();
					}
					
					m.incrDenom();
					if ( flgLab ) m.incrNum();
				}
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	
	void doCCR( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgProvided = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			
			// don't care about visit
			Intervention p = null;
			
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.PT_ECOPY_REQUEST )
						&& withinDateRange( p.getDate(), startDate, endDate )){	
					
					for ( Reca reca2 = medpt.getInterventionsReca(); Reca.isValid( reca2 ); ){
						Intervention q = new Intervention( reca2 );
						if (( q.getType() == Intervention.Types.PT_ECOPY_PROVIDED )
							&& ( q.getDate().compare( p.getDate()) <= 3 ) && ( q.getDate().compare( p.getDate()) >= 0 )){
							flgProvided = true;
							break;
						}
						reca2 = q.getLLHdr().getLast();
					}
					
					m.incrDenom();
					if ( flgProvided ) m.incrNum();
				}
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	void doMedRec( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgReconcile = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			
			// don't care about visit
			Intervention p = null;
			
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.PT_TRANSFER_IN )
						&& withinDateRange( p.getDate(), startDate, endDate )){	
					
					for ( Reca reca2 = medpt.getInterventionsReca(); Reca.isValid( reca2 ); ){
						Intervention q = new Intervention( reca2 );
						if (( q.getType() == Intervention.Types.MEDICATION_RECONCILIATION )
							&& ( q.getDate().compare( p.getDate()) <= 3 ) && ( q.getDate().compare( p.getDate()) >= 0 )){
							flgReconcile = true;
							break;
						}
						reca2 = q.getLLHdr().getLast();
					}
					
					m.incrDenom();
					if ( flgReconcile ) m.incrNum();
				}
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	


	void doTransferOutSummary( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgSummary = false;
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			
			// don't care about visit
			Intervention p = null;
			
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.PT_TRANSFER_OUT )
						&& withinDateRange( p.getDate(), startDate, endDate )){	
					
					for ( Reca reca2 = medpt.getInterventionsReca(); Reca.isValid( reca2 ); ){
						Intervention q = new Intervention( reca2 );
						if (( q.getType() == Intervention.Types.SUMMARY_PROVIDER )
							&& ( q.getDate().compare( p.getDate()) <= 3 )  && ( q.getDate().compare( p.getDate()) >= 0 )){
							flgSummary = true;
							break;
						}
						reca2 = q.getLLHdr().getLast();
					}
					
					m.incrDenom();
					if ( flgSummary ) m.incrNum();
				}
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	

	
	
	void doVisitSummary( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgProvided = false;
			Vitals v = null;
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			for ( Reca reca = medpt.getVitalsReca(); Reca.isValid( reca ); reca = v.getLLHdr().getLast()){				
				v = new Vitals( reca );				
				if ( withinDateRange( v.getDate(), startDate, endDate )){		
					
					for ( Reca reca2 = medpt.getInterventionsReca(); Reca.isValid( reca2 ); ){
						Intervention q = new Intervention( reca2 );
						if (( q.getType() == Intervention.Types.VISIT_SUMMARY )
							&& ( q.getDate().compare( v.getDate()) <= 3 ) && ( q.getDate().compare( v.getDate()) >= 0 )){
							flgProvided = true;
							break;
						}
						reca2 = q.getLLHdr().getLast();
					}
					
					m.incrDenom();
					if ( flgProvided ) m.incrNum();
				}
			}
		}
		
		dirPt.close();
		

		return;
	}
	
	
	
	

	void doReminders( Measure m, Date startDate, Date endDate ){
		
		System.out.println( m.getName() + "  " + m.getDesc());

		DirPt dirPt = DirPt.open();
		dirPt.setRec( new Rec( startRec ));	
		
		while( dirPt.getNext()){

			boolean flgInt = false;
			
			
			int ptage = Date.getAgeYears( dirPt.getBirthdate(), startDate );
			if (( ptage > 5 ) && ( ptage < 65 )) continue;
			
			
			
			
			MedPt medpt = new MedPt( dirPt.getMedRec());

			
			// was there a visit within the date range? (go back one year)
			if ( ! hadVisit( medpt, new Date( 1, 1, startDate.getYear() - 1 ), endDate )) continue;
			
			
			
			Intervention p = null;
			
			for ( Reca reca = medpt.getInterventionsReca(); Reca.isValid( reca ); reca = p.getLLHdr().getLast()){
				p = new Intervention( reca );
				if (( p.getType() == Intervention.Types.REMINDER_SENT )
						&& withinDateRange( p.getDate(), startDate, endDate )){					
					flgInt = true;
					break;
				}
			}

			m.incrDenom();
			if ( flgInt ) m.incrNum();
		}
		
		dirPt.close();
		

		return;
	}
	
	
	



}
