package org.drools.testing.plugin.forms;

import java.io.BufferedReader;
import java.io.FileReader;

import org.drools.testing.core.beans.TestSuite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.exolab.castor.xml.Unmarshaller;

public class InputForm extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm form;

	public InputForm(FormEditor editor) {
		super(editor, "Input Capture","Rtl Input Capture"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		
		TestSuite testSuite;
		try {
			FileEditorInput fileEditorInput = ((FileEditorInput)getEditorInput());
			BufferedReader br = new BufferedReader(new FileReader(fileEditorInput.getFile().getName()));
			Unmarshaller unmarshaller = new Unmarshaller();
			testSuite = (TestSuite) unmarshaller.unmarshal(br);
		}catch (Exception e) {	
			e.printStackTrace();
		}
		
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText("Rtl Input Capture"); //$NON-NLS-1$
		//form.setBackgroundImage(FormArticlePlugin.getDefault().getImage(FormArticlePlugin.IMG_FORM_BG));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		//createTableSection(form, toolkit, Messages.getString("SecondPage.firstSection")); //$NON-NLS-1$
		//createTableSection(form, toolkit, Messages.getString("SecondPage.secondSection"));		 //$NON-NLS-1$
	}
	
	public void init(IEditorSite site, IEditorInput input) {
		setSite(site);
		setInput(input);
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.drools.testing.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}
}
