package palmed;

import java.util.Calendar;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Flashchart;
import org.zkoss.zul.Window;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.XYModel;

import usrlib.MyChartEngine;
import usrlib.Rec;
import usrlib.Reca;
import usrlib.UnitHelpers;






public class VitalsWinController extends GenericForwardComposer {

	private Window vitalsWin;			// autowired
	private Listbox vitalsListbox;		// autowired
	private Div divChart;				// autowired
	
	private Chart vitalsChart;			// NOT autowired
	private String chartSeries = "";	// series displayed in current chart
	
	private Rec	ptRec;					// patient record number
	private Reca vitalsReca;			// vitals first (latest) Reca link
	private usrlib.Date bdate;			// birthdate
	private palmed.Sex sex;
	
	private Window chartWin;			// autowired
	private Listbox graphListbox;		// autowired
	
	

	
	
	
	
	public void doAfterCompose( Component component ){
		
		// Call superclass to do autowiring
		try {
			super.doAfterCompose( component );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Components.wireVariables( vitalsHtmlWin, this );
//		Components.addForwards( vitalsHtmlWin, this );

		
		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if ( exec != null ){
			Map myMap = exec.getArg();
			if ( myMap != null ){
				try{ ptRec = (Rec) myMap.get( "ptRec" ); } catch ( Exception e ) { /* ignore */ };
			}
		}
		

		
		// make sure we have a patient
		if (( ptRec == null ) || ( ptRec.getRec() < 2 )){
			System.out.println( "Error: VitalsWinController() invalid or null ptRec" );
			ptRec = new Rec( 2 );		// default for testing
		}

		//System.out.println( "VitalsWinController() ptRec=" + ptRec.getRec());
		
		
		
		
		// populate list
		refreshList();

		return;
	}
	
	
	
	
	
	
	
	// Refresh list and graphed data
	
	private void refreshList(){
		
		if ( vitalsListbox == null ) return;
		
		// remove all items
		for ( int i = vitalsListbox.getItemCount(); i > 0; --i ){
			vitalsListbox.removeItemAt( 0 );
		}
		
		// populate list
		DirPt dirPt = new DirPt( ptRec );
		bdate = dirPt.getBirthdate();
		sex = dirPt.getSex();
		
		MedPt medPt = new MedPt( dirPt.getMedRec());
		vitalsReca = medPt.getVitalsReca();

		// finished if there are no vitals to read
		if (( vitalsReca == null ) || ( vitalsReca.getRec() < 2 )) return;

		
		Vitals vitals = null;
		
		for ( Reca reca = vitalsReca; reca.getRec() != 0; reca = vitals.getLLHdr().getLast()){

			vitals = new Vitals( reca );
			if ( vitals.getStatus() != Vitals.Status.CURRENT ) continue;
			
			// create new Listitem and add cells to it
			Listitem item;
			(item = new Listitem()).setParent( vitalsListbox );
			item.setValue( reca );		// set reca into listitem for later retrieval
	
			// Get vitals from data struct
			// Convert units if necessary
			// TODO - which units?????
			// Don't set data if ZERO - that is a non-entered value
			
			String s;
			double d;
			int i;

			new Listcell( vitals.getDate().getPrintable(9)).setParent( item );

			d = vitals.getTemp();
			s = "";
			if ( d > 1 ){
				if ( true ) d = UnitHelpers.getFahrenheit( d );
				s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			}
			new Listcell( s ).setParent( item );
			
			i = vitals.getPulse();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			new Listcell( s ).setParent( item );
			
			i = vitals.getResp();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			new Listcell( s ).setParent( item );
			
			s = vitals.getBP();
			new Listcell( s ).setParent( item );
			
			d = vitals.getHeight();
			if ( true ) d = UnitHelpers.getInches( d );
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			new Listcell( s ).setParent( item );
			
			d = (double) vitals.getWeight();
			if ( true ) d = UnitHelpers.getPoundsFromGrams( d );
			//if ( true ) d = UnitHelpers.getKilogramsFromGrams( d );
			s = ( d == 0 ) ? "": String.format( "%6.1f", d );
			new Listcell( s ).setParent( item );
			
			d = vitals.getHead();
			if ( true ) d = UnitHelpers.getInches( d );
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			new Listcell( s ).setParent( item );
			
			i = vitals.getPO2();
			s = ( i == 0 ) ? "": String.format( "%d", i );
			new Listcell( s ).setParent( item );
			
			d = ClinicalCalculations.computeBMI( vitals.getWeight(), vitals.getHeight());
			s = ( d == 0 ) ? "": String.format( "%4.1f", d );
			new Listcell( s ).setParent( item );
		}
		
		return;
	}
	
	
	
	
	// Open dialog to enter new vitals data
	
	public void onClick$newvitals( Event ev ){
		
		if ( VitalsEdit.enter( ptRec, vitalsWin )){
			refreshList();
		}
		return;
	}
	
	
	
	// Open dialog to edit existing vitals data
	
	public void onClick$edit( Event ev ){
	
		// was an item selected?
		if ( vitalsListbox.getSelectedCount() < 1 ){
			try { Messagebox.show( "No item selected." ); } catch (InterruptedException e) { /*ignore*/ }
			return;
		}

		// get selected item's reca
		Reca reca = (Reca) vitalsListbox.getSelectedItem().getValue();
		if (( reca == null ) || ( reca.getRec() < 2 )) return;

		// call edit routine
		if ( VitalsEdit.edit( reca, ptRec, vitalsWin )){
			refreshList();
		}		
		return;
	}
	
	
	
	
	
	
	
	// These methods handle Graphing the Vitals Data

	// method called to put vitals data into the chart
	interface AddVitalsData { boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units units, UnitHelpers.Units yUnits ); }	
	// method called to put standard growth data into the chart
	interface AddGrowthData { void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ); }



	public void onSelect$graphListbox( Event ev ){

		String s_graph = graphListbox.getSelectedItem().getId();
		
		if ( s_graph.equals( "graphTemp" )){
			chartTemp();
		} else if ( s_graph.equals( "graphHt" )){
			chartHeight();
		} else if ( s_graph.equals( "graphWt" )){
			chartWeight();
		} else if ( s_graph.equals( "graphHC" )){
			chartHC();
		} else if ( s_graph.equals( "graphP" )){
			chartP();
		} else if ( s_graph.equals( "graphR" )){
			chartR();
		} else if ( s_graph.equals( "graphPO2" )){
			chartPO2();
		} else if ( s_graph.equals( "graphBP" )){
			chartBP();
		} else if ( s_graph.equals( "graphBMI" )){
			chartBMI();
		} else if ( s_graph.equals( "growthWt" )){
			growthWt();
		} else if ( s_graph.equals( "growthHt" )){
			growthHt();
		} else if ( s_graph.equals( "growthBMI" )){
			growthBMI();
		} else if ( s_graph.equals( "growthWtHt" )){
			growthWtHt();
		} else if ( s_graph.equals( "infantWt" )){
			infantWt();
		} else if ( s_graph.equals( "infantLen" )){
			infantLen();
		} else if ( s_graph.equals( "infantHC" )){
			infantHC();
		} else if ( s_graph.equals( "infantWtLen" )){
			infantWtLen();
		}
		
		return;
	}

			

		
		
		
		
		//public void onClick$g_temp( Event ev ){
		public void chartTemp(){

			chartThis( "Temp", "Vitals: Temerature", "deg F", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = UnitHelpers.getFahrenheit( v.getTemp());
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Temp", new Long( v.getDate().getJavaDate().getTime()), d );
				        return true;
					}
					return false;
				}
			});
		}

		public void chartHeight(){
			
			chartThis( "Height", "Vitals: Height", "inches", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = UnitHelpers.getInches( (double) v.getHeight());
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Height", new Long( v.getDate().getJavaDate().getTime()), d );		
				        return true;
					}
					return false;
				}
			});
		}

		public void chartWeight(){
			
			chartThis( "Weight", "Vitals: Weight", "lbs", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = UnitHelpers.getPoundsFromGrams( (double) v.getWeight());
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Weight", new Long( v.getDate().getJavaDate().getTime()), d );	
				        return true;
					}
					return false;
				}
			});
		}

		public void chartHC(){
			
			chartThis( "Head", "Vitals: Head Circumference", "inches", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = UnitHelpers.getInches( (double) v.getHead());
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Head", new Long( v.getDate().getJavaDate().getTime()), d );			
				        return true;
					}
					return false;
				}
			});
		}

		public void chartP(){
			
			chartThis( "Pulse", "Vitals: Pulse Rate", "beats/min", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = (double)  v.getPulse();
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Pulse", new Long( v.getDate().getJavaDate().getTime()), d );	
				        return true;
					}
					return false;
				}
			});
		}

		public void chartR(){
			
			chartThis( "Resp", "Vitals: Respirations", "resp/min", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = (double)  v.getResp();
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "Resp", new Long( v.getDate().getJavaDate().getTime()), d );
				        return true;
					}
					return false;
				}
			});
		}

		public void chartPO2(){
			
			chartThis( "pO2", "Vitals: Pulse Oximetery", "%", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double d = (double) v.getPO2();
					if ( d != 0 ){		// don't graph zero values				
				        m.addValue( "pO2", new Long( v.getDate().getJavaDate().getTime()), d );		
				        return true;
					}
					return false;
				}
			});
		}

		public void chartBP(){
			
			chartThis( "BP", "Vitals: Blood Pressure", "mmHg", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel chartmodel, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double sbp = v.getSBP();
					double dbp = v.getDBP();

					if (( sbp != 0 ) && ( dbp != 0 )){		// don't graph zero values				
				        chartmodel.addValue( "SBP", new Long( v.getDate().getJavaDate().getTime()), sbp );			
				        chartmodel.addValue( "DBP", new Long( v.getDate().getJavaDate().getTime()), dbp );	
				        return true;
					}
					return false; 
				}
			});
		}
		

		public void chartBMI(){
			
			chartThis( "BMI", "Vitals: Body Mass Index", "kg/m2", new AddVitalsData() {
				public boolean addVitals( Vitals v, XYModel chartmodel, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
					double bmi = ClinicalCalculations.computeBMI( v.getWeight(), v.getHeight());

					if ( bmi != 0 ){		// don't graph zero values				
				        chartmodel.addValue( "BMI", new Long( v.getDate().getJavaDate().getTime()), bmi );	
				        return true;
					}
					return false; 
				}
			});
		}
		



		
		
		
		
		
		
		
		
	public void growthWt(){
		
		UnitHelpers.Units units = UnitHelpers.Units.POUNDS;
				
		growthChart( "pedWt", "Growth: Age vs Weight", "Weight", units, Chart.YEAR, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = UnitHelpers.getPoundsFromGrams( (double) v.getWeight());
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Weight", usrlib.Date.getAgeMonths( bdate, v.getDate())/12.0, d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addWtForAge(m, male, xUnits );
			}
		}, this.bdate );
		
		return;
	}

	public void growthHt(){
		
		UnitHelpers.Units units = UnitHelpers.Units.INCHES;
		
		
		growthChart( "pedHt", "Growth: Age vs Height", "Height", units, Chart.YEAR, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = UnitHelpers.getInches( (double) v.getHeight());
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Height", usrlib.Date.getAgeMonths( bdate, v.getDate())/12.0, d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addHtForAge(m, male, xUnits );
			}
		},this.bdate );
		
		return;
	}

	public void growthBMI(){
		
		growthChart( "pedBMI", "Growth: Age vs BMI", "BMI", UnitHelpers.Units.BMI, Chart.YEAR, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = ClinicalCalculations.computeBMI( (double) v.getWeight(), (double) v.getHeight());;
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "BMI", usrlib.Date.getAgeMonths( bdate, v.getDate())/12.0, d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addBMIvsAge(m, male );
			}
		},this.bdate );
		
		return;
	}

	
	
	
	public void growthWtHt(){
		
		UnitHelpers.Units xUnits = UnitHelpers.Units.INCHES;
		UnitHelpers.Units yUnits = UnitHelpers.Units.POUNDS;
		
		growthChartXY( "pedHtWt", "Growth: Height vs Weight", "Height", xUnits, "Weight", yUnits, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double h = (double) v.getHeight();
				if ( xUnits == UnitHelpers.Units.INCHES ) h = UnitHelpers.getInches( h );
				double w = (double) v.getWeight();
				if ( yUnits == UnitHelpers.Units.POUNDS ) w = UnitHelpers.getPoundsFromGrams( w );
				if (( h != 0 ) && ( w != 0 )){		// don't graph zero values				
			        m.addValue( "chart", h, w );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addHtVsWt( m, male, xUnits, yUnits );
			}
		});
		
		return;
	}

	
	
	
	
	
	
	
	
	
	
	public void infantWt(){
		
		UnitHelpers.Units units = UnitHelpers.Units.POUNDS;
		
		
		growthChart( "infantWt", "Growth: Age vs Weight", "Weight", units, Chart.MONTH, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = UnitHelpers.getPoundsFromGrams( (double) v.getWeight());
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Weight", usrlib.Date.getAgeMonths( bdate, v.getDate()), d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addWtForAgeInfant(m, male, xUnits );
			}
		},this.bdate );
		
		return;
	}

	public void infantLen(){
		
		UnitHelpers.Units units = UnitHelpers.Units.INCHES;
		
		
		growthChart( "infantLen", "Growth: Age vs Length", "Length", units, Chart.MONTH, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = UnitHelpers.getInches( (double) v.getHeight());
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Length", usrlib.Date.getAgeMonths( bdate, v.getDate()), d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addLenForAgeInfant(m, male, xUnits );
			}
		},this.bdate );
		
		return;
	}

	public void infantHC(){
		
		UnitHelpers.Units units = UnitHelpers.Units.CENTIMETERS;
		
		
		growthChart( "infantHC", "Growth: Age vs Head Circumference", "Head Circ", units, Chart.MONTH, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double d = (double) v.getHead();
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Head Circ", usrlib.Date.getAgeMonths( bdate, v.getDate()), d );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addHeadForAgeInfant(m, male, xUnits );
			}
		},this.bdate );
		
		return;
	}

	public void infantWtLen(){
		
		UnitHelpers.Units xUnits = UnitHelpers.Units.INCHES;
		UnitHelpers.Units yUnits = UnitHelpers.Units.POUNDS;
		
		
		
		
		growthChartXY( "infantWtLen", "Growth: Length vs Weight", "Length", xUnits, "Weight", yUnits, new AddVitalsData() {
			public boolean addVitals( Vitals v, XYModel m, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				double h = (double) v.getHeight();
				if ( xUnits == UnitHelpers.Units.INCHES ) h = UnitHelpers.getInches( h );
				double w = (double) v.getWeight();
				if ( yUnits == UnitHelpers.Units.POUNDS ) w = UnitHelpers.getPoundsFromGrams( w );
				if (( h != 0 ) && ( w != 0 )){		// don't graph zero values				
			        m.addValue( "chart", h, w );	
			        return true;
				}
				return false;
			}
		}, new AddGrowthData() {
			public void addGrowth( XYModel m, boolean male, UnitHelpers.Units xUnits, UnitHelpers.Units yUnits ){
				GrowthStandard.addLenVsWtInfant(m, male, xUnits, yUnits );
			}
		});
		
		return;
	}
	

	
/*	
	//public void onClick$g_temp( Event ev ){
	public void onSelect$graphListbox( Event ev ){

		chartThis( "Temp", "deg F", new Foo() {
			public boolean bar( Vitals v, XYModel m ){
				double d = UnitHelpers.getFahrenheit( v.getTemp());
				if ( d != 0 ){		// don't graph zero values				
			        m.addValue( "Temp", new Long( v.getDate().getJavaDate().getTime()), d );
			        return true;
				}
				return false;
			}
		});
	}

*/	

/*	public void chartThis( String series, Foo b ){
		
		System.out.println( "in chartThis(), series=" + series );
		
		// already displaying this chart?
		if ( chartSeries.equals( series )) return;
		
		// already a chart being displayed?  Get rid of it.
		if ( vitalsChart != null ) vitalsChart.detach();
		vitalsChart = null;
		System.out.println( "in onClick$g_wt 2");
		
		
        // set this chart series
        chartSeries = series;
        
        //CategoryModel catmodel = new SimpleCategoryModel();
        XYModel chartmodel = new SimpleXYModel();

		for ( Reca reca = vitalsReca; reca.getRec() != 0; ){
			
			Vitals vitals = new Vitals( reca );
			double d = b.bar( vitals );
			
			//double d = UnitHelpers.getPoundsFromGrams( (double) vitals.getWeight());
			
			if ( d != 0 ){		// don't graph zero values				
		        chartmodel.addValue( series, new Long( vitals.getDate().getJavaDate().getTime()), d );			
			}
			
			// get next reca in list	
			reca = vitals.getLLHdr().getLast();
		}

        vitalsChart = new Chart();
        vitalsChart.setType("time_series");
        vitalsChart.setTitle( "Vitals Chart" );
        vitalsChart.setPeriod( Chart.DAY );
        vitalsChart.setDateFormat( "MM/dd/yyyy" );
        vitalsChart.setWidth( "600px" );
        vitalsChart.setHeight( "400px" );
        //vitalsChart.setTimeFormat( "" );
        //vitalsChart.setWidth( Integer( div_center.getWidth()) - 10 );
        //System.out.println( divChart.getWidth() );
        vitalsChart.setModel(chartmodel); 
        vitalsChart.setParent( chartWin );
		System.out.println( "in chartThis done");

	}
*/
	
	
	public void chartThis( String series, String title, String units, AddVitalsData b ){
		
		System.out.println( "in chartThis(), series=" + series );
		
		// already displaying this chart?
		if ( chartSeries.equals( series )) return;
		
		// already a chart being displayed?  Get rid of it.
		if ( vitalsChart != null ) vitalsChart.detach();
		vitalsChart = null;
		
		
        // set this chart series
        chartSeries = series;
        
        //CategoryModel chartmodel = new SimpleCategoryModel();
        XYModel chartmodel = new SimpleXYModel();

        Vitals vitals = null;
        
		for ( Reca reca = vitalsReca; reca.getRec() != 0; reca = vitals.getLLHdr().getLast()){
			
			vitals = new Vitals( reca );
			if ( vitals.getStatus() != Vitals.Status.CURRENT ) continue;

			b.addVitals( vitals, chartmodel, null, null /*UNITS*/ );
			
			//double d = UnitHelpers.getPoundsFromGrams( (double) vitals.getWeight());
			
			//if ( d != 0 ){		// don't graph zero values				
		    //    chartmodel.addValue( series, new Long( vitals.getDate().getJavaDate().getTime()), d );			
			//}
		}

		MyChartEngine my = new MyChartEngine();
		my.addSeries( 4, true, true );
		my.addSeries( 4, true, true );
        vitalsChart = new Chart();
        vitalsChart.setEngine(my); 
        vitalsChart.setType("time_series");
        vitalsChart.setTitle( title );
        vitalsChart.setYAxis( series + " " + units );
        vitalsChart.setXAxis( "Date" );
        vitalsChart.setPeriod( Chart.DAY );
        vitalsChart.setDateFormat( "MM/dd/yyyy" );
        vitalsChart.setWidth( "600px" );
        vitalsChart.setHeight( "400px" );
        //vitalsChart.setTimeFormat( "" );
        //vitalsChart.setWidth( Integer( div_center.getWidth()) - 10 );
        //System.out.println( divChart.getWidth() );
        vitalsChart.setModel(chartmodel); 
        vitalsChart.setParent( chartWin );
		System.out.println( "in chartThis done");

	}
	
	
	
	
	
	
	
	
	
	public void growthChart( String series, String title, String yAxisLabel, UnitHelpers.Units units, String timeUnits, AddVitalsData b, AddGrowthData s, usrlib.Date bdate ){
		
		int dataCount = 0;
		
		System.out.println( "in growthChart(), series=G" + series );
		
		// already displaying this chart?
		if ( chartSeries.equals( "G" + series )) return;
		
		// already a chart being displayed?  Get rid of it.
		if ( vitalsChart != null ) vitalsChart.detach();
		vitalsChart = null;
		
		
        // set this chart series
        chartSeries = "G" + series;
        
        //CategoryModel chartmodel = new SimpleCategoryModel();
        XYModel chartmodel = new SimpleXYModel();

        Vitals vitals = null;
		for ( Reca reca = vitalsReca; reca.getRec() != 0; reca = vitals.getLLHdr().getLast()){
			
			vitals = new Vitals( reca );
			if ( vitals.getStatus() != Vitals.Status.CURRENT ) continue;
			
			if ( b.addVitals( vitals, chartmodel, units, null )) ++dataCount;
		}
		
		s.addGrowth( chartmodel, (sex == palmed.Sex.MALE) ? true: false, units, null );

		MyChartEngine my = new MyChartEngine();
		if ( dataCount > 0 ) my.addSeries( 4, false, true );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );

        vitalsChart = new Chart();
        vitalsChart.setEngine(my); 
        vitalsChart.setType( "line" );		//"time_series");
        vitalsChart.setTitle( title );
        vitalsChart.setYAxis( yAxisLabel + " " + units.getLabel() );
        vitalsChart.setXAxis( "Age in " + ((timeUnits == Chart.YEAR) ? "Years": "Months") );
        vitalsChart.setPeriod( timeUnits );
        vitalsChart.setDateFormat( "MM/dd/yyyy" );
        vitalsChart.setWidth( "600px" );
        vitalsChart.setHeight( "400px" );
        vitalsChart.setPaneColor( (sex == palmed.Sex.MALE) ? "#89CFFD": "#FFCBDB" );
        vitalsChart.setPaneAlpha( 100 );
        //vitalsChart.setBgColor( (sex == palmed.Sex.MALE) ? "#89CFFD": "#FFCBDB" );
        //vitalsChart.setBgAlpha( 140 );
        //vitalsChart.setTimeFormat( "" );
        //vitalsChart.setWidth( Integer( div_center.getWidth()) - 10 );
        //System.out.println( divChart.getWidth() );
        vitalsChart.setModel(chartmodel); 
        vitalsChart.setParent( chartWin );
		System.out.println( "in chartThis done");

	}
	


	public void growthChartXY( String series, String title, String xAxisLabel, UnitHelpers.Units xAxisUnits, String yAxisLabel, UnitHelpers.Units yAxisUnits, AddVitalsData b, AddGrowthData s ){
		
		int dataCount = 0;
		
		System.out.println( "in growthChart(), series=G" + series );
		
		// already displaying this chart?
		if ( chartSeries.equals( "G" + series )) return;
		
		// already a chart being displayed?  Get rid of it.
		if ( vitalsChart != null ) vitalsChart.detach();
		vitalsChart = null;
		
		
        // set this chart series
        chartSeries = "G" + series;
        
        //CategoryModel chartmodel = new SimpleCategoryModel();
        XYModel chartmodel = new SimpleXYModel();
        
        Vitals vitals = null;

		for ( Reca reca = vitalsReca; reca.getRec() != 0; reca = vitals.getLLHdr().getLast()){
			
			vitals = new Vitals( reca );
			if ( vitals.getStatus() != Vitals.Status.CURRENT ) continue;
			
			if ( b.addVitals( vitals, chartmodel, xAxisUnits, yAxisUnits )) ++dataCount;
		}
		
		s.addGrowth( chartmodel, (sex == palmed.Sex.MALE) ? true: false, xAxisUnits, yAxisUnits );

		MyChartEngine my = new MyChartEngine();
		if ( dataCount > 0 ) my.addSeries( 4, false, true );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );
		my.addSeries( 1, true, false );

        vitalsChart = new Chart();
        vitalsChart.setEngine(my); 
        vitalsChart.setType("line");
        vitalsChart.setTitle( title );
        vitalsChart.setXAxis( xAxisLabel + " " + xAxisUnits.getLabel() );
        vitalsChart.setYAxis( yAxisLabel + " " + yAxisUnits.getLabel() );
        vitalsChart.setWidth( "600px" );
        vitalsChart.setHeight( "400px" );
        vitalsChart.setPaneColor( (sex == palmed.Sex.MALE) ? "#89CFFD": "#FFCBDB" );
        vitalsChart.setPaneAlpha( 100 );
        //vitalsChart.setTimeFormat( "" );
        //vitalsChart.setWidth( Integer( div_center.getWidth()) - 10 );
        //System.out.println( divChart.getWidth() );
        vitalsChart.setModel(chartmodel); 
        vitalsChart.setParent( chartWin );
		System.out.println( "in chartThis done");

	}
	
	

	
	
	
	
	
	
	
	
	
	
	
}
