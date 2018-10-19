package palmed;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import usrlib.Rec;

public class StdDictWinController extends GenericForwardComposer {

	private Radio rbActive; // autowired
	private Radio rbInactive; // autowired
	private Radio rbAll; // autowired
	private Listheader status; // autowired
	private Window stdDictWin; // autowired

	private static final long serialVersionUID = 393417207647876991L;


	private Listbox stdDictListbox; // autowired

	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshList();

	}

	// Watch for radiobutton to change
	public void onCheck$rbActive(Event ev) {
		refreshList();
	}

	public void onCheck$rbInactive(Event ev) {
		refreshList();
	}

	public void onCheck$rbAll(Event ev) {
		refreshList();
	}

	// Refresh listbox when needed
	public void refreshList() {

		// which kind of list to display (from radio buttons)
		StdDictSoap.DisplayRecords display = StdDictSoap.DisplayRecords.Active;

		if (rbActive.isSelected())
			display = StdDictSoap.DisplayRecords.Active;
		else if (rbInactive.isSelected())
			display = StdDictSoap.DisplayRecords.Inactive;
		else if (rbAll.isSelected())
			display = StdDictSoap.DisplayRecords.All;

		// display status column header?
		status.setVisible((display == StdDictSoap.DisplayRecords.All) ? true
				: false);

		// remove all items
		for (int i = stdDictListbox.getItemCount(); i > 0; --i) {
			stdDictListbox.removeItemAt(0);
		}

		StdDictSoap.fillListbox(stdDictListbox, display, true);

	}

	// Open dialog to restore record, unused for now
	@SuppressWarnings("unused")
	private void onClick$btnRestore(Event ev) throws InterruptedException {

		// get selected item's rec
		Rec rec = (Rec) stdDictListbox.getSelectedItem().getValue();
		if ((rec == null) || (rec.getRec() < 2))
			return;

		// mark user inactive/hidden
		StdDictSoap stdDict = new StdDictSoap(rec);

		stdDict.setStatusActive();
		stdDict.write(rec);
		// log the access
		AuditLogger.recordEntry(AuditLog.Action.STDDICT_EDIT, null,
				Pm.getUserRec(), rec, stdDict.getAbbr());
		// refresh list
		refreshList();

	}

	// Open dialog to edit existing standard dictation

	public void onClick$btnEdit(Event ev) {

		// was an item selected?
		if (stdDictListbox.getSelectedCount() < 1) {
			try {
				Messagebox.show("No item selected.");
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		// get selected item's rec
		Rec rec = (Rec) stdDictListbox.getSelectedItem().getValue();
		if ((rec == null) || (rec.getRec() < 2))
			return;

		// call edit routine
		if (StdDictEdit.edit(rec, true, stdDictWin)) {
			refreshList();
		}

	}

	public void onClick$btnNew(Event ev) {

		// call edit routine
		StdDictEdit.add(true, stdDictWin);
		refreshList();

	}

	// Open dialog to remove an existing standard dictation
	public void onClick$btnDelete(Event ev) throws InterruptedException {

		// was an item selected?
		if (stdDictListbox.getSelectedCount() < 1) {
			Messagebox.show("No item selected.");
			return;
		}

		// is user sure?
		if (Messagebox.show("Do you really wish to delete this entry?",
				"Delete Standard Dictation", Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION) != Messagebox.YES)
			return;

		// get selected item's rec
		Rec rec = (Rec) stdDictListbox.getSelectedItem().getValue();
		if ((rec == null) || (rec.getRec() < 2))
			return;

		// mark user inactive/hidden
		StdDictSoap stdDict = new StdDictSoap(rec);

		stdDict.setStatusInactive();
		stdDict.write(rec);

		// log the access
		AuditLogger.recordEntry(AuditLog.Action.STDDICT_DELETE, null,
				Pm.getUserRec(), rec, stdDict.getAbbr());

		// refresh list
		refreshList();
	}
}
