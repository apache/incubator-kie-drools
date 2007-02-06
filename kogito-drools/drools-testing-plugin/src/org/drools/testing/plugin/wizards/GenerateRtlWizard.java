package org.drools.testing.plugin.wizards;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.drools.lang.descr.RuleDescr;
import org.drools.testing.core.beans.Scenario;
import org.drools.testing.core.beans.TestSuite;
import org.drools.testing.core.exception.RuleTestLanguageException;
import org.drools.testing.core.main.Testing;
import org.drools.testing.plugin.model.RtlModel;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "rtl". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class GenerateRtlWizard extends Wizard implements INewWizard {
	private RtlNewPage rtlNewPage;
	private SelectRulesPage selectRulesPage;
	private ISelection selection;
	private RtlModel rtlModel;
	
	/**
	 * Constructor for RtlWizard.
	 */
	public GenerateRtlWizard() {
		super();
		setNeedsProgressMonitor(true);
		setRtlModel(new RtlModel());
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		rtlNewPage = new RtlNewPage(selection);
		selectRulesPage = new SelectRulesPage(selection);
		addPage(rtlNewPage);
		addPage(selectRulesPage);
		
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		
		Object[] ruleDescrs = selectRulesPage.getViewer().getCheckedElements();
		for (int i=0; i<ruleDescrs.length; i++) {
			rtlModel.getRuleDescrs().add((RuleDescr) ruleDescrs[i]);
		}
		
		final String fileName = rtlNewPage.getRtlFileName()+".rtl";
		final String containerName = rtlNewPage.getContainerName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,	
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		//String containerName = "test";
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName +  "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		
		try {
			Testing testing = new Testing("The Test Test Suite", rtlModel.getPackageDescr());
			Scenario scenario = testing.generateScenario("Scenario One",rtlModel.getPackageDescr().getRules());
			testing.addScenarioToSuite(scenario);
			TestSuite testSuite = testing.getTestSuite();
			FileWriter out = new FileWriter(fileName);
			Marshaller marshaller = new Marshaller(out);
        	marshaller.setSuppressXSIType(true);
        	marshaller.setSupressXMLDeclaration(true);
        	marshaller.marshal(testSuite);
        	out.close();
        	
        	InputStream stream = openContentStream(fileName);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		}catch (RuleTestLanguageException e) {
			throwCoreException(e.getMessage());
			
		} catch (IOException e) {
			throwCoreException(e.getMessage());
		}catch (MarshalException e) {
			throwCoreException(e.getMessage());
		}catch (ValidationException e) {
			throwCoreException(e.getMessage());
		}
		
		
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with the newly generated rtl scenario
	 */

	private InputStream openContentStream(String fileName) throws CoreException {
		String contents = "";
		try {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
    	String line;
    	while ((line = br.readLine()) != null) {
    		contents = contents + line;
		}
		}catch (Exception e) {
			throwCoreException(e.getMessage());
		}
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.drools.testing.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	public RtlModel getRtlModel() {
		return rtlModel;
	}

	public void setRtlModel(RtlModel rtlModel) {
		this.rtlModel = rtlModel;
	}

	public RtlNewPage getRtlNewPage() {
		return rtlNewPage;
	}

	public void setRtlNewPage(RtlNewPage rtlNewPage) {
		this.rtlNewPage = rtlNewPage;
	}

	public SelectRulesPage getSelectRulesPage() {
		return selectRulesPage;
	}

	public void setSelectRulesPage(SelectRulesPage selectRulesPage) {
		this.selectRulesPage = selectRulesPage;
	}
}