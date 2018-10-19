package palmed;

import java.util.ArrayList;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class StdDictEditWinController extends GenericForwardComposer {

	private final class EventListenerImplementation implements EventListener {
		public void onEvent(Event ev) throws Exception {
			ev.stopPropagation();
			onClick$cancel(ev);
		}
	}

	/**
	 * 
	 */

	Rec rec = null; // std Dictation record
	StdDictEdit.Operation operation = null; // operation to perform

	Tabpanel tabpanel; // tab panel this window is in

	Window editStdDict; // autowired - window

	Button save; // autowired - save button
	Button cancel; // autowired - cancel button

	Textbox stdDictAbbr;
	Textbox stdDictSubjective;
	Textbox stdDictObjective;
	Textbox stdDictAssessment;
	Textbox stdDictPlan;
	Radio rbActive; // autoewired
	Radio rbInactive;// autowired

	public class TextData {
		public String dataDB; // text in the file
		public String dataUI;// text in the text box
		public Rec recordID; // text record ID
		public int offsetID; // offset (1-4)
	}

	public void doAfterCompose(Component component) {

		// Call superclass to do autowiring
		try {
			super.doAfterCompose(component);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get arguments from map
		Execution exec = Executions.getCurrent();

		if (exec != null) {
			Map<?, ?> myMap = exec.getArg();
			if (myMap != null) {
				try {
					operation = (StdDictEdit.Operation) myMap.get("operation");
				} catch (Exception e) { /* ignore */

				}

				try {
					rec = (Rec) myMap.get("rec");
				} catch (Exception e) { /* ignore */
				}

			}
		}

		// Is window in a Tabpanel???
		// Get Tabpanel this window is placed in.
		// May have to go several layers deep due to enclosing window.
		if (component instanceof Tabpanel) {
			tabpanel = (Tabpanel) component;
		} else if (component.getParent() instanceof Tabpanel) {
			tabpanel = (Tabpanel) component.getParent();
		} else if (component.getParent().getParent() instanceof Tabpanel) {
			tabpanel = (Tabpanel) component.getParent().getParent();
		}

		// Add close tabpanel event listener
		if (tabpanel != null) {
			tabpanel.getLinkedTab().addEventListener("onClose",
					new EventListenerImplementation());
		}

		if ((operation != null) && (operation == StdDictEdit.Operation.New)) {

			rbActive.setSelected(true);

		} else { // operation == EditPt.Operation.EDITPT

			if ((rec == null) || rec.getRec() < 2) {
				rec = new Rec(2); // default for testing
			}

			// Read Standard dictation info
			StdDictSoap stdDict = new StdDictSoap(rec);

			stdDictAbbr.setValue(stdDict.getAbbr().trim());
			char status = stdDict.getStatus();
			if (status == StdDictSoap.Status.Current) {
				rbActive.setSelected(true);
			} else if (status == StdDictSoap.Status.Removed) {
				rbInactive.setSelected(true);
			}
			stdDictSubjective.setValue(stdDict.getText(1).trim());
			stdDictObjective.setValue(stdDict.getText(2).trim());
			stdDictAssessment.setValue(stdDict.getText(3).trim());
			stdDictPlan.setValue(stdDict.getText(4).trim());
		}
	}

	/**
	 * Save
	 * 
	 */

	public boolean save() {

		if ((operation != null) && (operation == StdDictEdit.Operation.New)) {
			//log.debug("Create new record");

		} else {

			// make sure we have a record
			if (rec.getRec() < 2) {
				System.out.println("Error: EditPtWinController().save() rec="
						+ rec.getRec());
				rec = new Rec(2); // default for testing
			}

		}

		StdDictSoap stdDict;
		// Save (write) records back to database
		if ((operation == StdDictEdit.Operation.New) || (rec == null)
				|| (rec.getRec() == 0)) {

			stdDict = new StdDictSoap();

			if (rbActive.isSelected() == true) {
				stdDict.setStatusActive();
			} else {
				stdDict.setStatusInactive();
			}
			String abbr = stdDictAbbr.getValue().trim();
			if (abbr.length() == 0) {
				try {
					Messagebox.show("No abbreviation is specified.", "Data entry error", Messagebox.OK, Messagebox.ERROR);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			} else {
				stdDict.setAbbr(abbr);
			}
			// make sure it is unique in the system
			if (StdDictSoap.findAbbrRec(abbr) == true) {
				try {
					Messagebox.show("Duplicate standard dictation. Please edit the existig one.", "Data entry error", Messagebox.OK, Messagebox.ERROR);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;

			}
			ArrayList<TextData> textData = new ArrayList<TextData>();

			boolean noTextData = true; // always assume failure
			for (int index = 1; index <= 4; index++) {
				TextData data = new TextData();
				data.dataDB = null;
				data.recordID = new Rec(0);
				data.offsetID = index;
				switch (index) {
				case 1:
					data.dataUI = stdDictSubjective.getValue().trim();
					break;
				case 2:
					data.dataUI = stdDictObjective.getValue().trim();
					break;
				case 3:
					data.dataUI = stdDictAssessment.getValue().trim();
					break;
				case 4:
					data.dataUI = stdDictPlan.getValue().trim();
					break;
				}
				if (data.dataUI.length() > 0) {
					noTextData = false; // something is there
				}

				textData.add(data);
			}

			if (noTextData == true) {
				try {
					Messagebox.show("No standard dictations are specified.", "Data entry error", Messagebox.OK, Messagebox.ERROR);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;

			}
			for (TextData elt : textData) {
				if (elt.dataUI.length() > 0) {
					// write text, get record, set record number to original
					// file
					Rec newRec = stdDict.writeText(elt.dataUI);
					stdDict.setTextRec(newRec, elt.offsetID);
				}
			}
			// write the actual record
			rec = stdDict.newRec();

		} else {

			//
			// get the current record from database
			stdDict = new StdDictSoap(rec);

			boolean dataDirty = false;

			// compare current values with the data from the text fields
			char statusDB = stdDict.getStatus();
			char status = StdDictSoap.Status.Current;
			if (rbInactive.isSelected() == true) {
				status = StdDictSoap.Status.Removed;
			}

			// new status is inactive now, ignore everything else and close
			// window
			if (status != statusDB) {
				dataDirty = true;
			}
			// status is changed and now is inactive, ignore
			if (status == StdDictSoap.Status.Removed) {
				stdDict.setStatusInactive();
			} else {
				stdDict.setStatusActive();
			}

			String abbrDB = stdDict.getAbbr().trim();
			String abbr = stdDictAbbr.getValue().trim();
			if (abbr.compareTo(abbrDB) != 0) {
				dataDirty = true;
				stdDict.setAbbr(abbr);
			}
			if (abbr.length() == 0) {
				try {
					Messagebox.show("No abbreviation is specified.", "Data entry error", Messagebox.OK, Messagebox.ERROR);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

			// make sure it is unique in the system
			if (StdDictSoap.findAbbrRec(abbr) == true) {
				try {
					Messagebox.show("Duplicate standard dictation. Please edit the existig one.", "Data entry error", Messagebox.OK, Messagebox.ERROR);
				} catch ( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;

			}

			ArrayList<TextData> textData = new ArrayList<TextData>();

			boolean noTextData = true; // always assume failure
			for (int index = 1; index <= 4; index++) {
				TextData data = new TextData();
				data.dataDB = stdDict.getText(index).trim();
				data.recordID = stdDict.getTextRec(index);
				data.offsetID = index;
				switch (index) {
				case 1:
					data.dataUI = stdDictSubjective.getValue().trim();
					break;
				case 2:
					data.dataUI = stdDictObjective.getValue().trim();
					break;
				case 3:
					data.dataUI = stdDictAssessment.getValue().trim();

					break;
				case 4:
					data.dataUI = stdDictPlan.getValue().trim();
					break;
				}
				if (data.dataUI.length() > 0) {
					noTextData = false; // something is there
				}

				textData.add(data);
			}
			
			if (noTextData == true) {
				try {
					Messagebox.show("No standard dictations are specified.", "Data entry error", Messagebox.OK, Messagebox.ERROR );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;

			}

			boolean textDirty = false;
			for (TextData elt : textData) {
				if (elt.dataDB.compareTo(elt.dataUI) != 0) {
					// / data changed
					textDirty = true;
					// write text, get record, set record number to original
					// file
					Rec newRec = stdDict.writeText(elt.dataUI);
					stdDict.setTextRec(newRec, elt.offsetID);
				}
			}
			
			if (dataDirty == true || textDirty == true) {
				stdDict.write(rec);
			}

		}

		return true;
	}

	public void onClose$editStdDict(Event e) throws InterruptedException {
		onClick$cancel(e);
	}

	public void onClick$save(Event e) throws InterruptedException {
		if (Messagebox.show("Save this information?", "Save?", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {

			if (save()) {
				closeWin();
			}

		} else {

			if (Messagebox.show("Continue editing?", "Continue?",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION) == Messagebox.NO) {
				closeWin();
			}
		}

	}

	public void onClick$cancel(Event e) throws InterruptedException {

		if (Messagebox.show("Leave without saving?", "Leave?", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION) == Messagebox.YES) {

			closeWin();
		}
	}

	private void closeWin() {

		editStdDict.detach();

	}

}

/**/
