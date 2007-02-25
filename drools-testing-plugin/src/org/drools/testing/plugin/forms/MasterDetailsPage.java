/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.drools.testing.plugin.forms;
import org.drools.testing.plugin.resources.Messages;
import org.drools.testing.plugin.resources.TestResourcesPlugin;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
/**
 * @author dejan
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class MasterDetailsPage extends FormPage {
	private TestSuitePropertiesBlock block;
	public MasterDetailsPage(FormEditor editor) {
		super(editor, "fourth", Messages.getString("MasterDetailsPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
		block = new TestSuitePropertiesBlock(this);
	}
	protected void createFormContent(final IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		//FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("MasterDetailsPage.title")); //$NON-NLS-1$
		form.setBackgroundImage(TestResourcesPlugin.getDefault().getImage(
				TestResourcesPlugin.IMG_FORM_BG));
		block.createContent(managedForm);
	}
	public TestSuitePropertiesBlock getBlock() {
		return block;
	}
}