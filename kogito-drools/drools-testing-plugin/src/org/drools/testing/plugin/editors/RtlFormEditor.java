package org.drools.testing.plugin.editors;

import java.io.BufferedReader;
import java.io.FileReader;

import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.plugin.forms.InputForm;
import org.drools.testing.plugin.forms.MasterDetailsPage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.exolab.castor.xml.Unmarshaller;

public class RtlFormEditor extends FormEditor {

	private TestSuite testSuite;
	
	public RtlFormEditor() {
		
	}

	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(display);
	}

	protected void addPages() {
		initialiseModel();
		try {
			TextEditor editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, EditorConstants.EditorPageTitles.TITLE_FREE_FORM);
			addPage(new MasterDetailsPage(this));
			addPage(new InputForm(this));
		}
		catch (PartInitException e) {
			//
		}
	}

	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setInput(editor.getEditorInput());
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
	
	private void initialiseModel () {
		try {
			FileEditorInput fileEditorInput = ((FileEditorInput)getEditorInput());
			BufferedReader br = new BufferedReader(new FileReader(fileEditorInput.getFile().getName()));
			Unmarshaller unmarshaller = new Unmarshaller(TestSuite.class);
			testSuite = (TestSuite) unmarshaller.unmarshal(br);
		}catch (Exception e) {	
			MessageDialog.openError(this.getSite().getShell(), "Error", e.getMessage());
		}
	}

	public TestSuite getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(TestSuite testSuite) {
		this.testSuite = testSuite;
	}
	
}
