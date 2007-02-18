package org.drools.testing.plugin.wizards;

import org.drools.testing.plugin.model.RtlModel;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (rtl).
 */

public class RtlNewPage extends WizardPage {
	
	private Text rtlFileText;
	
	private Text fileText;
	
	private Text containerText;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public RtlNewPage(ISelection selection) {
		super("wizardPage");
		setTitle("Rtl Creation");
		setDescription("This wizard creates a new file with *.rtl extension that can be opened by a multi-page editor.\n" +
				"Please enter the location of the Drools drl file.");
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		
		label = new Label(container, SWT.NULL);
		label.setText("&Drl:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		Button buttonFileName = new Button(container, SWT.PUSH);
		buttonFileName.setText("Browse...");
		buttonFileName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleFileBrowse();
				rtlFileText.setText(fileText.getText());
				stripIllegalChars(rtlFileText);
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Rtl name:");

		rtlFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		rtlFileText.setLayoutData(gd);
		rtlFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
		
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		
	}

	private void handleFileBrowse () {
		FileDialog dialog = new FileDialog(getShell());
		String[] extensions = {"*.drl"};
		dialog.setFilterExtensions(extensions);
		dialog.setFilterPath(".");
		dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerName()).getFullPath().toString());
		fileText.setText(dialog.open());
		
		


	}
	
	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}
	
	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		String fileName = getFileName();
		String rtlFileName = rtlFileText.getText();
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
		.findMember(new Path(getContainerName()));

		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (rtlFileName.length() == 0) {
			updateStatus("Rtl file name must be specified");
			return;
		}
		if (rtlFileName.indexOf(".") != -1) {
			updateStatus("Rtl file name must be valid");
			return;
		}
		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("drl") == false) {
				updateStatus("File extension must be \"drl\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public String getRtlFileName() {
		return rtlFileText.getText();
	}
	
	public String getContainerName() {
		return containerText.getText();
	}
	
	public IWizardPage getNextPage()
	{
		saveDataToModel();
		SelectRulesPage page = ((GenerateRtlWizard)getWizard()).getSelectRulesPage();
		page.onEnterPage();
		return page;
	}
	
	public void saveDataToModel () {
		GenerateRtlWizard wizard = (GenerateRtlWizard)getWizard();
		RtlModel model = wizard.getRtlModel();
		model.setFileName(fileText.getText());
	}
	
	private void stripIllegalChars (Text txt) {
		
		String x = txt.getText();
		if (x.indexOf("\\") != -1 || x.indexOf(".") != -1) {
			x =	txt.getText().substring(
					txt.getText().lastIndexOf("\\")+1, 
					txt.getText().indexOf("."));
		}
		txt.setText(x);
	}
}