package org.drools.testing.plugin.forms;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class InputForm extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm form;

	public InputForm(FormEditor editor) {
		super(editor, "Input Capture","Rtl Input Capture"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected void createFormContent(IManagedForm managedForm) {
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


}
