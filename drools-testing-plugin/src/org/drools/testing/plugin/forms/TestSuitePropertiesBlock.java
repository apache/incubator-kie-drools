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
import java.util.ArrayList;

import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.plugin.editors.RtlFormEditor;
import org.drools.testing.plugin.resources.Messages;
import org.drools.testing.plugin.resources.TestResourcesPlugin;
import org.drools.testing.plugin.utils.LoadModel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
/**
 *
 */
public class TestSuitePropertiesBlock extends MasterDetailsBlock {
	
	private FormPage page;
	private TableViewer viewer;
	private Composite parent;
	
	public TestSuitePropertiesBlock(FormPage page) {
		this.page = page;
	}
	/**
	 * @param id
	 * @param title
	 */
	class MasterContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			ArrayList ch = new ArrayList();
			if (inputElement instanceof TestSuite) {
				TestSuite testSuite = (TestSuite) inputElement;
				ch.add(testSuite);
				Scenario[] scenarios = testSuite.getScenario();
				for (int i=0; i<scenarios.length; i++)
					ch.add((Scenario) scenarios[i]);
				/*
				Rule[] rules = testSuite.getRules();
				for (int i=0; i<rules.length; i++)
					ch.add((Rule) rules[i]);
				*/	
				return ch.toArray();
			}
			return new Object[0];
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	class MasterLabelProvider extends LabelProvider
			implements
				ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof Scenario)
				return ((Scenario)obj).getName();
			else if (obj instanceof TestSuite)
				return ((TestSuite)obj).getName();
			else
				return obj.toString();
		}
		public Image getColumnImage(Object obj, int index) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		this.parent = parent;
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
		section.setText(Messages.getString("TestSuitePropertiesBlock.sname")); //$NON-NLS-1$
		section
				.setDescription(Messages.getString("TestSuitePropertiesBlock.sdesc")); //$NON-NLS-1$
		section.marginWidth = 10;
		section.marginHeight = 5;
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		Table t = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		Button b = toolkit.createButton(client, Messages.getString("TestSuitePropertiesBlock.add"), SWT.PUSH); //$NON-NLS-1$
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		b.setLayoutData(gd);
		section.setClient(client);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		viewer = new TableViewer(t);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		viewer.setContentProvider(new MasterContentProvider());
		viewer.setLabelProvider(new MasterLabelProvider());
		viewer.setInput(((RtlFormEditor)page.getEditor()).getTestSuite());
		viewer.getTable().getDisplay()
			.asyncExec(new Runnable() {
				public void run () {
					updateTableTreeFromTextEditor();
				}
			});
	}
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		Action haction = new Action("hor", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
				form.reflow(true);
			}
		};
		haction.setChecked(true);
		haction.setToolTipText(Messages.getString("TestSuitePropertiesBlock.horizontal")); //$NON-NLS-1$
		haction.setImageDescriptor(TestResourcesPlugin.getDefault()
				.getImageRegistry()
				.getDescriptor(TestResourcesPlugin.IMG_HORIZONTAL));
		Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
				form.reflow(true);
			}
		};
		vaction.setChecked(false);
		vaction.setToolTipText(Messages.getString("TestSuitePropertiesBlock.vertical")); //$NON-NLS-1$
		vaction.setImageDescriptor(TestResourcesPlugin.getDefault()
				.getImageRegistry().getDescriptor(TestResourcesPlugin.IMG_VERTICAL));
		form.getToolBarManager().add(haction);
		form.getToolBarManager().add(vaction);
	}
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(TestSuite.class, new TestSuiteDetailsPage());
		detailsPart.registerPage(Scenario.class, new ScenarioDetailsPage());
	}
	
	private void updateTableTreeFromTextEditor () {
		TextEditor textEditor = ((RtlFormEditor)page.getEditor()).getTextEditor();
		String content = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
		try {
			viewer.setInput(LoadModel.loadTestSuite(content));
		}catch (Exception e) {
			MessageDialog.openError(parent.getShell(), "Error", e.getMessage());
		}
	}
}