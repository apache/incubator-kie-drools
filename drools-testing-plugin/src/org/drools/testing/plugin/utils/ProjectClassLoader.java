package org.drools.testing.plugin.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class ProjectClassLoader {
	
	public static URLClassLoader getProjectClassLoader(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IProject project = ((IFileEditorInput) input).getFile().getProject();
			IJavaProject javaProject = JavaCore.create(project);
			return getProjectClassLoader(javaProject);
		}
		return null;
	}

    public static URLClassLoader getProjectClassLoader(IJavaProject project) {
        List pathElements = getProjectClassPathURLs(project);
        URL urlPaths[] = (URL[]) pathElements.toArray(new URL[pathElements.size()]);
        return new URLClassLoader(urlPaths, Thread.currentThread().getContextClassLoader());
    }

    private static URL getRawLocationURL(IPath simplePath)
            throws MalformedURLException {
        File file = getRawLocationFile(simplePath);
        return file.toURL();
    }

    private static File getRawLocationFile(IPath simplePath) {
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(simplePath);
        File file = null;
        if (resource != null) {
            file = ResourcesPlugin.getWorkspace().getRoot().findMember(
                    simplePath).getRawLocation().toFile();
        } else {
            file = simplePath.toFile();
        }
        return file;
    }

    public static List getProjectClassPathURLs(IJavaProject project) {
        List pathElements = new ArrayList();
        try {
            IClasspathEntry[] paths = project.getResolvedClasspath(true);
            if (paths != null) {
                
                for ( int i = 0; i < paths.length; i++ ) {
                    IClasspathEntry path = paths[i];
                    if (path.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
                        URL url = getRawLocationURL(path.getPath());
                        pathElements.add(url);
                    }
                }
            }
            IPath location = getProjectLocation(project.getProject());
            IPath outputPath = location.append(project.getOutputLocation()
                    .removeFirstSegments(1));
            pathElements.add(outputPath.toFile().toURL());
            
            // also add classpath of required projects
            String[] names = project.getRequiredProjectNames();
            for ( int i = 0; i < names.length; i++ ) {
                String projectName = names[i];
                IProject reqProject = project.getProject().getWorkspace()
                    .getRoot().getProject(projectName);
                if (reqProject != null) {
                    IJavaProject reqJavaProject = JavaCore.create(reqProject);
                    pathElements.addAll(getProjectClassPathURLs(reqJavaProject));
                }
            }
        } catch (JavaModelException e) {
            //DroolsIDEPlugin.log(e);
        } catch (MalformedURLException e) {
            //DroolsIDEPlugin.log(e);
        }
        return pathElements;
    }
    
    public static IPath getProjectLocation(IProject project) {
        if (project.getRawLocation() == null) {
            return project.getLocation();
        } else {
            return project.getRawLocation();
        }
    }
}
