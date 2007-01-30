package org.drools.testing.plugin.editors;

import org.drools.testing.plugin.wizards.GenerateRtlWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class RTLEditorContributor extends MultiPageEditorActionBarContributor {
	private IEditorPart activeEditorPart;
	private Action generateRtlAction;
	/**
	 * Creates a multi-page contributor.
	 */
	public RTLEditorContributor() {
		super();
		createActions();
	}
	/**
	 * Returns the action registed with the given text editor.
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}
	/* (non-JavaDoc)
	 * Method declared in AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
		if (actionBars != null) {

			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

			actionBars.setGlobalActionHandler(
				ActionFactory.DELETE.getId(),
				getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(
				ActionFactory.UNDO.getId(),
				getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.REDO.getId(),
				getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.CUT.getId(),
				getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(
				ActionFactory.COPY.getId(),
				getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(
				ActionFactory.PASTE.getId(),
				getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(
				ActionFactory.SELECT_ALL.getId(),
				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			actionBars.setGlobalActionHandler(
				ActionFactory.FIND.getId(),
				getAction(editor, ITextEditorActionConstants.FIND));
			actionBars.setGlobalActionHandler(
				IDEActionFactory.BOOKMARK.getId(),
				getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();
		}
	}
	private void createActions() {
		generateRtlAction = new Action () {
			public void run() {
				GenerateRtlWizard generateRtlWizard = new GenerateRtlWizard();
				activeEditorPart.getSite().getSelectionProvider().getSelection();
				generateRtlWizard.init(PlatformUI.getWorkbench(), new StructuredSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection()));
				WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						generateRtlWizard);
				dialog.open();
			}
		};
		generateRtlAction.setText(EditorConstants.ToolBar.GENERATE);
		generateRtlAction.setToolTipText(EditorConstants.ToolBar.GENERATE_TOOLTIP);
		generateRtlAction.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), EditorConstants.ToolBar.GENERATE_IMG));
		
	}
	public void contributeToMenu(IMenuManager manager) {
		
	}
	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(generateRtlAction);
	}
}
