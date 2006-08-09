/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     C�dric Chabanois (cchabanois@ifrance.com) - modified for Subversion
 *     Philippe Ombredanne for nexB Inc. - drools hack
 *     See POM: comments for sections changed/commented/added
 *     mostly copied from revision 2077
 *     @see org.tigris.subversion.subclipse.ui.actions.CommitAction
 *     @see org.tigris.subversion.subclipse.ui.actions.SetSvnPropertyAction
 *     @linkplain http://subclipse.tigris.org/svn/subclipse/trunk/subclipse/ui
 *     Set a property and performs a commit in one pass with dialogs
 *******************************************************************************/
package org.nexb.easyeclipse.drools.deployer.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.team.core.TeamException;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.commands.GetStatusCommand;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.core.util.Util;
import org.tigris.subversion.subclipse.ui.Policy;
import org.tigris.subversion.subclipse.ui.actions.WorkspaceAction;
import org.tigris.subversion.subclipse.ui.dialogs.CommitDialog;
import org.tigris.subversion.subclipse.ui.operations.CommitOperation;
import org.tigris.subversion.subclipse.ui.settings.ProjectProperties;
import org.tigris.subversion.svnclientadapter.ISVNStatus;
import org.tigris.subversion.svnclientadapter.SVNStatusUtils;

/**
 * Action for checking in files to a subversion provider
 * Prompts the user for a release comment, and shows a selection
 * list of added and modified resources, including unversioned resources.
 * If selected, unversioned resources will be added to version control,
 * and committed.
 */
public abstract class SetSvnPropertyAndDeployAction extends WorkspaceAction {
   
	//protected static final String COMMIT_COMMENT = "Deployed latest Drools Rule to repository";
    //POM: start
    public abstract String getPropertyName();
    public abstract String getPropertyValue();
    
	//POM:end

	protected String commitComment;
    protected IResource[] resourcesToCommit;
    protected String url;
    protected boolean hasUnaddedResources;
    protected boolean commit;
    protected boolean keepLocks;
    protected IResource[] selectedResources;

	/*
     * get non added resources and prompts for resources to be added
     * prompts for comments
     * add non added files
     * commit selected files
	 * @see SVNAction#execute(IAction)
	 */
	public void execute(IAction action) throws InvocationTargetException, InterruptedException {
		//only keep the 1st resource
		final IResource[] resources = new IResource[]{getSelectedResources()[0]};
		final ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resources[0]);

	    final List resourcesToBeAdded = new ArrayList();
	    final List resourcesToBeDeleted = new ArrayList();

		run(new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				/*
				 * (non-Javadoc)
				 * POM:This code borrowed from
				 * @see org.tigris.subversion.subclipse.ui.actions.SetSvnPropertyAction
				 */
				try {
					svnResource.setSvnProperty(getPropertyName(), getPropertyValue(), true); 
				} catch (SVNException e) {
					throw new InvocationTargetException(e);
				}
				/*
				 * (non-Javadoc)
				 * POM:end borrowing from
				 * @see org.tigris.subversion.subclipse.ui.actions.SetSvnPropertyAction
				 */

				try {
				    // search for modified or added, non-ignored resources in the selection.
				    IResource[] modified = getModifiedResources(resources, monitor);

				    // if no changes since last commit, do not show commit dialog.
				    if (modified.length == 0) {
					    MessageDialog.openInformation(getShell(), Policy.bind("CommitDialog.title"), Policy.bind("CommitDialog.noChanges")); //$NON-NLS-1$ //$NON-NLS-2$
					    commit = false;
					} else {
					    ProjectProperties projectProperties = ProjectProperties.getProjectProperties(modified[0]);
					    commit = confirmCommit(modified, projectProperties);
					}

				    // if commit was not canceled, create a list of any
				    // unversioned resources that were selected and a list of any missing
				    // resources that were selected.
					if (commit) {
					    for (int i = 0; i < resourcesToCommit.length; i++) {
					        IResource resource = resourcesToCommit[i];
					        ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resource);
					        if (!svnResource.isManaged()) resourcesToBeAdded.add(resource);
					        if (svnResource.getStatus().isMissing()) resourcesToBeDeleted.add(resource);
					    }
					}
				} catch (TeamException e) {
					throw new InvocationTargetException(e);
				}
			}
		}, true /* cancelable */, PROGRESS_BUSYCURSOR); //$NON-NLS-1$

		if (!commit) {
			return; // user canceled
		}

		new CommitOperation(getTargetPart(), resources,
				(IResource[]) resourcesToBeAdded.toArray(new IResource[resourcesToBeAdded.size()]),
				(IResource[]) resourcesToBeDeleted.toArray(new IResource[resourcesToBeDeleted.size()]),
				resourcesToCommit, commitComment, keepLocks).run();
	}

	/**
	 * get the modified and unadded resources in resources parameter
	 */
	protected IResource[] getModifiedResources(IResource[] resources, IProgressMonitor iProgressMonitor) throws SVNException {
	    final List modified = new ArrayList();
	    List unversionedFolders = new ArrayList();
		hasUnaddedResources = false;
	    for (int i = 0; i < resources.length; i++) {
			 IResource resource = resources[i];
			 ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resource);

			 // if only one resource selected, get url.  Commit dialog displays this.
			 if (resources.length == 1) {
				   url = svnResource.getStatus().getUrlString();
				   if ((url == null) || (resource.getType() == IResource.FILE)) url = Util.getParentUrl(svnResource);
			 }

			 // get adds, deletes, updates and property updates.
			 GetStatusCommand command = new GetStatusCommand(svnResource, true, false);
			 command.run(iProgressMonitor);
			 ISVNStatus[] statuses = command.getStatuses();
			 for (int j = 0; j < statuses.length; j++) {
			     if (SVNStatusUtils.isReadyForCommit(statuses[j]) || SVNStatusUtils.isMissing(statuses[j])) {
			         IResource currentResource = SVNWorkspaceRoot.getResourceFor(statuses[j]);
			         if (currentResource != null) {
			             ISVNLocalResource localResource = SVNWorkspaceRoot.getSVNResourceFor(currentResource);
			             if (!localResource.isIgnored()) {
			                 if (!SVNStatusUtils.isManaged(statuses[j])) {
			                 	hasUnaddedResources = true;
			                 	if ((currentResource.getType() != IResource.FILE) && !isSymLink(currentResource))
			                 		unversionedFolders.add(currentResource);
			                 	else
					                if (!modified.contains(currentResource)) modified.add(currentResource);
			                 } else
			                	 if (!modified.contains(currentResource)) modified.add(currentResource);
			             }
			         }
			     }
			 }
	    }
	    // get unadded resources and add them to the list.
	    IResource[] unaddedResources = getUnaddedResources(unversionedFolders, iProgressMonitor);
	    for (int i = 0; i < unaddedResources.length; i++)
	    	if (!modified.contains(unaddedResources[i])) modified.add(unaddedResources[i]);
	    return (IResource[]) modified.toArray(new IResource[modified.size()]);
	}

	/**
	 * prompt commit of selected resources.
	 * @throws SVNException
	 */
	protected boolean confirmCommit(IResource[] modifiedResources, ProjectProperties projectProperties) throws SVNException {
	   if (onTagPath(modifiedResources)) {
	       // Warning - working copy appears to be on a tag path.
	       if (!MessageDialog.openQuestion(getShell(), Policy.bind("DeployRuleDialog.title"), Policy.bind("CommitDialog.tag"))) //$NON-NLS-1$ //$NON-NLS-2$
	           return false;
	   }
       
/**       
//POM: modified to bypass the commit comment
//POM:	   CommitDialog dialog = new CommitDialog(getShell(), modifiedResources, url, hasUnaddedResources, projectProperties);
//POM:	   boolean commitOK = (dialog.open() == Window.OK);
	   url = null;
	   commitComment = COMMIT_COMMENT;
	   resourcesToCommit = modifiedResources;
	   keepLocks = false; //POM: should it be set to true? or use prefs?
	   return true;
       
*/
       
       CommitDialog dialog = new CommitDialog(getShell(), modifiedResources, url, hasUnaddedResources, projectProperties);
       boolean commitOK = (dialog.open() == Window.OK);
       
       url = null;
       commitComment = dialog.getComment();
       resourcesToCommit = dialog.getSelectedResources();
       keepLocks = dialog.isKeepLocks();
       return commitOK;
       
       
       
	}

	private boolean onTagPath(IResource[] modifiedResources) throws SVNException {
	    // Multiple resources selected.
	    if (url == null) {
			 IResource resource = modifiedResources[0];
			 ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(resource);
             String firstUrl = svnResource.getStatus().getUrlString();
             if ((firstUrl == null) || (resource.getType() == IResource.FILE)) firstUrl = Util.getParentUrl(svnResource);
             if (firstUrl.indexOf("/tags/") != -1) return true; //$NON-NLS-1$
	    }
	    // One resource selected.
        else if (url.indexOf("/tags/") != -1) return true; //$NON-NLS-1$
        return false;
    }

    /**
	 * @see org.tigris.subversion.subclipse.ui.actions.SVNAction#getErrorTitle()
	 */
//POM: use custom title
//	protected String getErrorTitle() {
//		return Policy.bind("SetSvnPropertyAndDeployAction.commitFailed"); //$NON-NLS-1$
//	}

	/**
	 * @see org.tigris.subversion.subclipse.ui.actions.WorkspaceAction#isEnabledForUnmanagedResources()
	 */
//POM: to enable property set , need resourec to be already managed
//	protected boolean isEnabledForUnmanagedResources() {
//		return false;
//	}

    /*
     *  (non-Javadoc)
     * @see org.tigris.subversion.subclipse.ui.actions.WorkspaceAction#isEnabledForInaccessibleResources()
     */
//POM: need to deal only with accessible resources
//    protected boolean isEnabledForInaccessibleResources() {
//        return true;
//    }

	/**
	 * get the unadded resources in resources parameter
	 */
	private IResource[] getUnaddedResources(List resources, IProgressMonitor iProgressMonitor) throws SVNException {
		final List unadded = new ArrayList();
		final SVNException[] exception = new SVNException[] { null };
		for (Iterator iter = resources.iterator(); iter.hasNext();) {
			IResource resource = (IResource) iter.next();
	        if (resource.exists()) {
			    // visit each resource deeply
			    try {
				    resource.accept(new IResourceVisitor() {
					public boolean visit(IResource aResource) {
						ISVNLocalResource svnResource = SVNWorkspaceRoot.getSVNResourceFor(aResource);
						// skip ignored resources and their children
						try {
							if (svnResource.isIgnored())
								return false;
							// visit the children of shared resources
							if (svnResource.isManaged())
								return true;
							if ((aResource.getType() == IResource.FOLDER) && isSymLink(aResource)) // don't traverse into symlink folders
								return false;
						} catch (SVNException e) {
							exception[0] = e;
						}
						// file/folder is unshared so record it
						unadded.add(aResource);
						return aResource.getType() == IResource.FOLDER;
					}
				}, IResource.DEPTH_INFINITE, false /* include phantoms */);
			    } catch (CoreException e) {
				    throw SVNException.wrapException(e);
			    }
			    if (exception[0] != null) throw exception[0];
	        }
		}
		if (unadded.size() > 0) hasUnaddedResources = true;
		return (IResource[]) unadded.toArray(new IResource[unadded.size()]);
	}

	protected boolean isSymLink(IResource resource) {
		File file = resource.getLocation().toFile();
	    try {
	    	if (!file.exists())
	    		return true;
	    	else {
	    		String cnnpath = file.getCanonicalPath();
	    		String abspath = file.getAbsolutePath();
	    		return !abspath.equals(cnnpath);
	    	}
	    } catch(IOException ex) {
	      return true;
	    }
	}
    protected IResource[] getSelectedResources() {
        if (selectedResources == null)
            return super.getSelectedResources();
        else
            return selectedResources;
    }

    public void setSelectedResources(IResource[] selectedResources) {
        this.selectedResources = selectedResources;
    }



	/*
	 * (non-Javadoc)
	 * POM:This code down to end borrowed from
	 * @see org.tigris.subversion.subclipse.ui.actions.SetSvnPropertyAction
	 */


	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.ui.actions.SVNAction#getErrorTitle()
	 */
	protected String getErrorTitle() {
		return Policy.bind("SetSvnPropertyAndDeployAction.set"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.ui.actions.WorkspaceAction#isEnabledForManagedResources()
	 */
	protected boolean isEnabledForManagedResources() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.ui.actions.WorkspaceAction#isEnabledForUnmanagedResources()
	 */
	protected boolean isEnabledForUnmanagedResources() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.ui.actions.WorkspaceAction#isEnabledForMultipleResources()
	 */
	protected boolean isEnabledForMultipleResources() {
		return false;
	}
}