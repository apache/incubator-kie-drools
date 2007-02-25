package org.drools.testing.plugin.editors;

import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.plugin.forms.MasterDetailsPage;
import org.drools.testing.plugin.forms.TestSuitePropertiesBlock;
import org.drools.testing.plugin.utils.LoadModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;

public class RtlFormEditor extends FormEditor {

	private TestSuite testSuite;
	private TextEditor textEditor;
	
	public RtlFormEditor() {
		
	}

	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(display);
	}

	protected void addPages() {
		initialiseModel();
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, EditorConstants.EditorPageTitles.TITLE_FREE_FORM);
			addPage(new MasterDetailsPage(this));
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
		return true;
	}
	
	private void initialiseModel () {
		try {
			LoadModel.loadTestSuite((FileEditorInput)getEditorInput());
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

	public TextEditor getTextEditor() {
		return textEditor;
	}

	public void setTextEditor(TextEditor textEditor) {
		this.textEditor = textEditor;
	}
	
	protected void pageChange ( int newPageIndex ) {
		switch (newPageIndex) {
			case 1 :
				TestSuitePropertiesBlock block = ((MasterDetailsPage)getSelectedPage()).getBlock();
				block.updateTableFromTextEditor();
				System.out.println("page change event running..");
				break;
		}
		super.pageChange(newPageIndex);
	}
	
}
