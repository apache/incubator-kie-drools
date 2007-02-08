package org.drools.testing.plugin.forms;

import java.io.BufferedReader;
import java.io.FileReader;

import org.drools.testing.core.beans.TestSuite;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.exolab.castor.xml.Unmarshaller;

public class InputForm extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm form;

	public InputForm(FormEditor editor) {
		super(editor, "Input Capture","Rtl Input Capture"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		
		TestSuite testSuite = null;
		try {
			FileEditorInput fileEditorInput = ((FileEditorInput)getEditorInput());
			BufferedReader br = new BufferedReader(new FileReader(fileEditorInput.getFile().getName()));
			Unmarshaller unmarshaller = new Unmarshaller(TestSuite.class);
			testSuite = (TestSuite) unmarshaller.unmarshal(br);
		}catch (Exception e) {	
			MessageDialog.openError(this.getSite().getShell(), "Error", e.getMessage());
		}
		
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText("Rtl Input Capture"); //$NON-NLS-1$
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		
		Section section = toolkit.createSection(form.getBody(), 
		Section.DESCRIPTION|Section.TITLE_BAR|
		Section.TWISTIE|Section.EXPANDED);
		TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;

		section.setLayoutData(td);
		section.addExpansionListener(new ExpansionAdapter() {
		public void expansionStateChanged(ExpansionEvent e) {
			form.reflow(true);
		  	}
		 });
		 section.setText("Test Suite");
		 section.setDescription("This is the test suite information "+
		      "generated from the supplied rtl file.");

		 Composite sectionClient = toolkit.createComposite(section);
		 sectionClient.setLayout(new GridLayout());
		 Label label = toolkit.createLabel(sectionClient, "Suite Name:");
		 Text text = toolkit.createText(sectionClient, testSuite.getName());
		 section.setClient(sectionClient);
		


		
	}
	
	public void init(IEditorSite site, IEditorInput input) {
		setSite(site);
		setInput(input);
	}

}
