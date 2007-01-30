package org.drools.testing.plugin.wizards;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.testing.plugin.listeners.TreeViewSelectionListener;
import org.drools.testing.plugin.wizards.model.RtlModel;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (rtl).
 */

public class SelectRulesPage extends WizardPage {
	
	private ISelection selection;
	
	private CheckboxTreeViewer viewer;
	
	private String fileName;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public SelectRulesPage(ISelection selection) {
		super("wizardPage");
		setTitle("Rtl Creation");
		setDescription("Please select the rules you wish to test.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		FillLayout layout = new FillLayout();
		container.setLayout(layout);
		layout.type = SWT.VERTICAL;
		
		viewer = new CheckboxTreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.addSelectionChangedListener(new TreeViewSelectionListener());
		initialize();
		setControl(container);
		
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 2)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
			}
		}
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getFileName() {
		return fileName;
	}
	
	public void onEnterPage () {
		
		GenerateRtlWizard wizard = (GenerateRtlWizard)getWizard();
		RtlModel model = wizard.getRtlModel();
		fileName = model.getFileName();
		File in = new File(getFileName());
		DrlParser drlParser = new DrlParser();
		try {
			Reader reader = new FileReader(in);
			PackageDescr packageDescr = drlParser.parse(reader);
			model.setPackageDescr(packageDescr);
			viewer.setInput(packageDescr);
		}catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	private void saveDataToModel() {
		GenerateRtlWizard wizard = (GenerateRtlWizard)getWizard();
		RtlModel model = wizard.getRtlModel();
		
	}
	
	
	class ViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			
		}
		
		public void dispose() {
		}
		
		public Object[] getElements(Object element) { 
			return getChildren(element); 
		}
		
		public Object getParent(Object element) {
			if (element instanceof PackageDescr)
				return ((PackageDescr)element);
			else
				return null;
		}
		
		/**
		 * This method has been modified heavily to just return the rule elements of the drl
		 */
		public Object[] getChildren(Object element)
	      {
			ArrayList ch = new ArrayList();
			PackageDescr packageDescr;
			try {
				packageDescr = (PackageDescr) element;
			}catch (ClassCastException e) {
				return ch.toArray();
			}
			
			List rules = packageDescr.getRules();
	        Iterator i = rules.iterator();
	        while (i.hasNext()) {
	        	RuleDescr ruleDescr = (RuleDescr) i.next();
	        	ch.add(ruleDescr);
	        }
	        
	        return ch.toArray();
	      }

		public boolean hasChildren(Object element) { 
			return getChildren(element).length > 0; 
		}

	}	
		class ViewLabelProvider extends LabelProvider {

		public String getText(Object element) {
			
			return ((RuleDescr)element).getName();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	public CheckboxTreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(CheckboxTreeViewer viewer) {
		this.viewer = viewer;
	}
}