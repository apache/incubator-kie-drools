package org.drools.testing.plugin.forms;

import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.plugin.editors.RtlFormEditor;
import org.drools.testing.plugin.resources.Messages;
import org.drools.testing.plugin.resources.TestResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class InputForm extends FormPage {
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	private TestSuite testSuite = null;

	public InputForm(FormEditor editor) {
		super(editor, "Input Capture","Rtl Input Capture"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		
		testSuite = ((RtlFormEditor) getEditor()).getTestSuite();
	
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("InputForm.title")); //$NON-NLS-1$
		form.setBackgroundImage(TestResourcesPlugin.getDefault().getImage(TestResourcesPlugin.IMG_FORM_BG));
		
		ColumnLayout layout = new ColumnLayout();
		layout.topMargin = 0;
		layout.bottomMargin = 5;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.maxNumColumns = 2;
		layout.minNumColumns = 1;
		form.getBody().setLayout(layout);
		
		createTestSuiteSection(managedForm, Messages.getString("InputForm.testSuiteSection"), Messages.getString("InputForm.testSuiteSection.descr"));
		
	}
	
	private void createTestSuiteSection (IManagedForm mform, String title, String desc) {
		
		Composite client = createSection(mform, title, desc, 2);
		FormToolkit toolkit = mform.getToolkit();
		toolkit.createLabel(client, Messages.getString("InputForm.testSuiteSection.nameLabel"));
		toolkit.createText(client, testSuite.getName());
		/*Button apply = toolkit.createButton(client, Messages.getString("InputForm.apply"), SWT.PUSH); //$NON-NLS-1$
		apply.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_BEGINNING));*/
	}
	
	private Composite createSection(IManagedForm mform, String title,
			String desc, int numColumns) {
		final ScrolledForm form = mform.getForm();
		FormToolkit toolkit = mform.getToolkit();
		Section section = toolkit.createSection(form.getBody(), Section.TWISTIE
				| Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		section.setText(title);
		section.setDescription(desc);
		//toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.numColumns = numColumns;
		client.setLayout(layout);
		section.setClient(client);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});
		return client;
	}
	
	public void init(IEditorSite site, IEditorInput input) {
		setSite(site);
		setInput(input);
	}
	
	

}
