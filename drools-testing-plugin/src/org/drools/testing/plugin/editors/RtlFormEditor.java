package org.drools.testing.plugin.editors;

import org.drools.testing.plugin.forms.InputForm;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RtlFormEditor extends FormEditor {

	public RtlFormEditor() {
		
	}

	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(display);
	}

	protected void addPages() {
		try {
			TextEditor editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, EditorConstants.EditorPageTitles.TITLE_FREE_FORM);
			addPage(new InputForm(this));
		}
		catch (PartInitException e) {
			//
		}
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}
	
}
