package org.drools.testing.plugin.action;

import org.drools.testing.plugin.wizards.GenerateRtlWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class RtlAction implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		GenerateRtlWizard generateRtlWizard = new GenerateRtlWizard();
		generateRtlWizard.init(PlatformUI.getWorkbench(), new StructuredSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()));
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				generateRtlWizard);
		dialog.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
