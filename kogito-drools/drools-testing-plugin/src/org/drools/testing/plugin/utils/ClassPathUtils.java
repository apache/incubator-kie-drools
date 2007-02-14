package org.drools.testing.plugin.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

public class ClassPathUtils {

	public ClassPathUtils () {
		
	}
	
	 public static URL[] getClasspathAsURLArray(IJavaProject javaProject) {
		if (javaProject == null)
			return null;
		Set visited = new HashSet();
		List urls = new ArrayList(20);
		collectClasspathURLs(javaProject, urls, visited, true);
		URL[] result = new URL[urls.size()];
		urls.toArray(result);
		return result;
	}

	private static void collectClasspathURLs(IJavaProject javaProject,
			List urls, Set visited, boolean isFirstProject) {
		if (visited.contains(javaProject))
			return;
		visited.add(javaProject);
		//IPath outPath = getJavaProjectOutputAbsoluteLocation(javaProject
		//		.getProject());
		IPath outPath = javaProject.getProject().getFullPath();
		outPath = outPath.addTrailingSeparator();
		URL out = createFileURL(outPath);
		urls.add(out);
		IClasspathEntry[] entries = null;
		try {
			entries = javaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			return;
		}
		IClasspathEntry entry, resEntry;
		IJavaProject proj = null;
		List projects = null;
		for (int i = 0; i < entries.length; i++) {
			entry = entries[i];
			switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_LIBRARY:
			case IClasspathEntry.CPE_CONTAINER:
			case IClasspathEntry.CPE_VARIABLE:
				collectClasspathEntryURL(entry, urls);
				break;
			case IClasspathEntry.CPE_PROJECT: {
				if (isFirstProject || entry.isExported())

					collectClasspathURLs(getJavaProject(entry), urls, visited,
							false);

				break;
			}
			}
		}
	}

	private static URL createFileURL(IPath path) {
		URL url = null;
		try {
			url = new URL("file://" + path.toOSString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	private static void collectClasspathEntryURL(IClasspathEntry entry,
			List urls) {
		URL url = createFileURL(entry.getPath());
		if (url != null)
			urls.add(url);
	}

	private static IJavaProject getJavaProject(IClasspathEntry entry) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
				entry.getPath().segment(0));
		if (proj != null)
			return getJavaProject((IClasspathEntry)proj);
		return null;
	}

}
