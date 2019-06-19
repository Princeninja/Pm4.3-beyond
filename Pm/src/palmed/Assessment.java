package palmed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;

import palmed.PhyExamWinController.Impressions;
import usrlib.Rec;

/**
 * <p>Title: PAL/MED </p>
 * 
 * <p>Description: Assessment/Impression class </p>
 * 
 * <p>Copyright: Copyright (c) 2019 </p>
 * 
 * <p>Company: PAL/MED </p>
 * 
 * @author Prince A Antigha
 * @version TBD
 */



public class Assessment  {
		
	 private List<Object> Imps = new ArrayList<Object>();
	 private Textbox Te30 = new Textbox();
	 private Textbox t30 = new Textbox();
	 private String typetxt = "";
	 private Component parent = null;
	 
	 private Window build_AssessWin = new Window();	 
	 
	 public Window setAssessvar(Component parent, int type){
		 
		 build_AssessWin.setParent(parent);
		 this.parent = parent;
		 
		 
		 switch ( type ){
		 case 0:
			 default:
				 typetxt = "Assessment";
				 break;
		
		case 1:
			typetxt = "Impression";
			break;
		 
		 }	
		 
		 return build_AssessWin;
	 }
	 
	 public void setfocus(Component onclickpar){
		 
		 onclickpar.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					t30.setFocus(true);
					
				}
				 
			 });
	 }
	 
	 private Listbox li30final = new Listbox();
	 private Button Add01 = new Button();
	 
	 public void createassessment( boolean noAssessment, final Rec ptRec, String Assess, boolean ISS){
		 
		 	//System.out.println("condition?: "+ noAssessment);
			if( noAssessment == true  ) {
			
			 Hbox hbox03 = new Hbox();
			 hbox03.setParent(build_AssessWin);
			
			 Groupbox gbox03 = new Groupbox();
			 gbox03.setWidth("380px");
			 gbox03.setParent(hbox03);
			 
			 new Caption (typetxt+"s: ").setParent(gbox03);
			
			 Hbox ButtonHbox00 = new Hbox();
			 ButtonHbox00.setParent(gbox03);
			 ButtonHbox00.setWidth(gbox03.getWidth());
			 ButtonHbox00.setPack("end");
			 
			 Button Add = new Button();
			 
			 Add.setParent(ButtonHbox00);
			 Add.setLabel("Add "+typetxt);
			 Add.setMold("trendy");
			 
			 Button Remove = new Button();
			 
			 Remove.setParent(ButtonHbox00);
			 Remove.setLabel("Remove "+typetxt+":");
			 Remove.setMold("trendy");
			 
			 final Listbox Impslist = new Listbox();
			 Impslist.setParent(ButtonHbox00);
			 Impslist.setRows(1);
			 Impslist.setMold("select");
			 
			 		 
			 Grid grid01 = new Grid();
			 grid01.setParent(gbox03);
			 
			 
			 Columns Columns00 = new Columns();
			 Columns00.setParent(grid01);
			 
			 Column Column00 = new Column();
			 Column00.setWidth("23px");
			 Column00.setParent(Columns00);
			 
			 Column Column01 = new Column();
			 Column01.setParent(Columns00);
			 
			 final Rows rows01 = new Rows();
			 rows01.setParent(grid01);
			 
			 Row row10 = new Row();
			 row10.setParent(rows01);
			 		 
			 
			 Label l10 = new Label();
			 l10.setValue("1 ");
			 l10.setParent(row10);
			 
			 t30 = new Textbox();
			 t30.setRows(2);
			 t30.setCols(50);
			 t30.setParent(row10);
			 
						 
			 
			
						 
			 
			 Add.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						// TODO Auto-generated method stub
						
						 Impressions Imp = new Impressions(rows01);
						 
						 int i = 2;
						 String num = Integer.toString(Imps.size()+i);  
						 
						 Imp.createImpression(num);
						 Imps.add(Imp);		
						 
						 Listitem jj = new Listitem();
						 jj.setLabel(num);
						 jj.setParent(Impslist);
						 
						 Impslist.setSelectedItem(Impslist.getItemAtIndex(0));
						
					}});
			 	 	 
			 		 
			 Remove.addEventListener(Events.ON_CLICK, new EventListener(){

				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					
					if ( Impslist.getSelectedCount() < 1 ){
						try {Messagebox.show( "No Assessment is currently available to remove. ");  } catch  (InterruptedException e) { /*ignore*/ }
					return;
					}
					
					int nos = Integer.parseInt(Impslist.getSelectedItem().getLabel().toString().trim()) - 2;
									
					Object O = Imps.get(nos);
													
					((Impressions) O).deleteImpression();
					
					if ( Imps.size() > nos +1 ){
					
					Object I = Imps.get(nos+1);
					((Impressions) I).newlbl(Impslist.getSelectedItem().getLabel().toString().trim());
									
					if ( Imps.size() > (nos+2) ){
						
						for ( int i=0; i < (Imps.size()-(nos+2)); i++) {
							
							int s = Integer.parseInt(Impslist.getSelectedItem().getLabel().toString().trim())+i;
													
							Object J = Imps.get(s);
													
							int sn = s+1;
							
							((Impressions) J).newlbl(Integer.toString(sn));
													
						}
						
					}
					
					}
								
					
					if ( Impslist.getItemCount() > (nos + 1)){
											
						Impslist.removeItemAt(Impslist.getItemCount()-1);
				
					}
					
					 else if( Impslist.getItemCount() <= (nos+1)) { 
									
						Impslist.removeItemAt(nos);	
					
					}
					
					Impslist.setSelectedItem(Impslist.getItemAtIndex(Impslist.getItemCount()-1));
					Impslist.setFocus(true);
					Imps.remove(O);
					
				
				}});
					 
			 Groupbox gbox05 = new Groupbox();
			 gbox05.setParent(hbox03);
			 gbox05.setMold("3d");
					 
			 new Caption("Current Problems: ").setParent(gbox05);
			 
			 final Listbox li30 = new Listbox();
			 li30.setRows(8);
			 li30.setParent(gbox05);
			 li30.setWidth("453px");
			 li30final = li30;
			 
			 ProbUtils.fillListbox(li30, ptRec);
			 
			 Hbox ButtonHbox01 = new Hbox();
			 ButtonHbox01.setParent(gbox05);
			 ButtonHbox01.setWidth("453px");
			 ButtonHbox01.setPack("center");
			 ButtonHbox01.setStyle("border: 1px solid teal");
			 
			 //Image arrow = new Image();
			 //arrow.setSrc("upleft.png");
			 
					 
			 
			 Add01 = new Button();
			 //Add01.appendChild(arrow);
			 //arrow.setParent(ButtonHbox01);
			 Add01.setLabel("Add to "+ typetxt);
			 Add01.setParent(ButtonHbox01);
			 Add01.setMold("trendy");
			 Add01.setImage( "upleft.png" );
			 //Add01.setHoverImage("upleft.png");
			
			 
			 Add01.addEventListener(Events.ON_CLICK, new EventListener(){
				
				
				public void onEvent(Event arg0) throws Exception {
					// TODO Auto-generated method stub
					
					
					if ( li30.getSelectedCount() < 1 ){
					try {Messagebox.show( "No Problem is currently selected. ");  } catch  (InterruptedException e) { /*ignore*/ }
						return;
				}		
					
					String Problem =  li30.getSelectedItem().getLabel().toString();
					
					boolean Duplicate  = false;
					
					if ( t30.getValue().contains(Problem) ){ Duplicate = true; }
					
					for ( int i=0; i < Imps.size(); i++ ){
						
					 if (((Impressions)  Imps.get(i)).text().contains(Problem)){ Duplicate = true ;}
						
					}
					
								
					if ( !Duplicate ){
					
					if ( t30.getValue().length() == 0 || t30.getValue().equals(" ") ){
						
						t30.setValue(Problem);
					}
					
									
					
					else {
					
					Impressions Imp01 = new Impressions(rows01);
					
					int i = 2;
					String num = Integer.toString(Imps.size()+i);  
					 
					Imp01.createImpression(num,Problem);
					Imps.add(Imp01);		
					 
					Listitem jj = new Listitem();
					jj.setLabel(num);
					jj.setParent(Impslist);
					
					}
					}
					
					else { try {Messagebox.show("An Impression with that problem already exists!");  } catch  (InterruptedException e) { /*ignore*/ }
				//	return;
					
					}
					
					
				 }});
			 
			 
			 
			if ( ISS ){
				
				 Button Add02 = new Button();
				 Add02.setLabel("Add a Problem");
				 Add02.setHeight("32px");
				 Add02.setParent(ButtonHbox01);
				 Add02.setMold("trendy");
				 Add02.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						
						Window PtChartwin = new Window();	
						
						
						PtChartwin.setParent(parent);
						PtChartwin.setPosition("center");
						
						PtChartwin.setHeight("822px");
						PtChartwin.setWidth("1013px");
						PtChartwin.setVflex("1");
						PtChartwin.setHflex("1");
						PtChartwin.setBorder("normal");
						
						PtChartwin.setTitle("Problems-Window");
						PtChartwin.setClosable(true);
						PtChartwin.setMaximizable(true);
														
						 Vbox  MVbox = new Vbox();
						 MVbox.setHeight("857px");
						 MVbox.setWidth("991px");
						 MVbox.setParent(PtChartwin);
						 
						 Groupbox PtGroupbox = new Groupbox();
						 PtGroupbox.setHflex( "1" );
						 PtGroupbox.setVflex( "1" );
						 PtGroupbox.setStyle("overflow:auto");
						 PtGroupbox.setMold("3d");
						 PtGroupbox.setParent( MVbox );
						 
						 //Create Problem window
						// pass parameters to new window
							HashMap<String, Object> myMap = new HashMap<String, Object>();
							myMap.put( "ptRec", (Rec)(ptRec ));	
							
							Window PtChartwin2;
							PtChartwin2 =  (Window) Executions.createComponents("probs.zul", PtGroupbox, myMap );
							
							try {
								PtChartwin.doModal();
							} catch (SuspendNotAllowedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							for ( int i = 0 ; i <  li30.getItemCount(); i++ ){
								
								li30.removeItemAt(i);
							
							}
							
							ProbUtils.fillListbox(li30, ptRec);
						
					
					}}); 
				 
				/* 
				 Button Add03 = new Button();
				 Add03.setLabel("Refresh Problems");
				 Add03.setParent(ButtonHbox01);
				 Add03.setMold("trendy");
				 Add03.addEventListener(Events.ON_CLICK, new EventListener(){

					public void onEvent(Event arg0) throws Exception {
						// TODO Auto-generated method stub
					
						for ( int i = 0 ; i <  li30.getItemCount(); i++ ){
						
							li30.removeItemAt(i);
						
						}
						
						ProbUtils.fillListbox(li30, ptRec);
						
					}}); 
				
				*/
				
				
				 
			}
			
							
			}
			 
			else if ( !noAssessment ) {
				
			Hbox hbox03 = new Hbox();
			hbox03.setParent(build_AssessWin);
				
			 Groupbox gbox06 = new Groupbox();
			 gbox06.setParent(hbox03);
			 gbox06.setMold("3d");
				 
			 new Caption("Assessment-Text: ").setParent(gbox06);
			 
			 Te30 = new Textbox();
			 Te30.setRows(8);
			 Te30.setParent(gbox06);
			 Te30.setWidth("353px"); 
			 Te30.setText(Assess);
			 
			}
			
		 
		 
	 }
	 
	public void onDoubleClick( Event ev ){  
			
		System.out.println("At doubleclick ");
			if ( !(li30final.getSelectedCount() < 1) ){
							
				onClick$Add01(ev); } } 
	 
	
	
	 private void onClick$Add01(Event ev) {
		
		 System.out.println("At on click Add01");
		 Add01.getEventHandler(Events.ON_CLICK);
		
	}

	public String getimpressions( boolean noAssessment){
		 
		 String ass = "";
			if ( noAssessment ){
			StringBuilder Impressions = new StringBuilder();
			
			Impressions.append("1:"+t30.getText());
			Impressions.append(System.getProperty("line.separator"));
			
			for ( int j = 0; j < Imps.size() ; j++ ){
				
				Impressions.append(((Impressions) Imps.get(j)).getTextN()+".  ");
				Impressions.append(System.getProperty("line.separator"));
			}
			
			
			 ass = Impressions.toString();}
			else {  ass = Te30.getText().trim(); }
		 
		System.out.println("final assessment text: "+ass);
		 return ass;
	 }
	 	
	 public class Impressions {
			
			Rows rows;
			Row row00;
			Label l00;
			Textbox t00;
			String id;
			
			Impressions(Rows rows){
				
				this.rows = rows;
			}	
				
			
			public void createImpression(String n){
				
				 row00 = new Row();
				 row00.setParent(rows);
					
				 id = (n);
				 l00 = new Label();
				 l00.setValue(id);
				 l00.setParent(row00);
				 		 
				 t00 = new Textbox();
				 t00.setRows(2);
				 t00.setCols(50);
				 t00.setParent(row00);
				 t00.setFocus(true);
				 
			}
			
			public void createImpression(String n, String Problem){
				
				row00 = new Row();
				row00.setParent(rows);
				
				id = n;
				l00 = new Label();
				l00.setValue(id);
				l00.setParent(row00);
				
				t00 = new Textbox();
				t00.setRows(2);
				t00.setCols(50);
				t00.setParent(row00);
				t00.setValue(Problem);	
				
			}
			
			public void deleteImpression(){
						
				row00.setVisible(false);
			}
			
			public void newlbl(String trim) {
				
				id = trim;
				l00.setValue(trim);
			}
			
			String text() {
						
				return t00.getValue();
				
			}
			public String getText(){
				
				String getText = "<p>"+id+":"+t00.getValue()+"</p>";
				
				return getText;
			}	
			
			public String getTextN(){
				
				String getText = id+":"+t00.getValue();
				
				return getText;
			}
			
		  }	
		
	 

}
