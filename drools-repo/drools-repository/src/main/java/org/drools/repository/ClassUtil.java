/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.drools.repository;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility methods to aid in class/resource loading.
 * 
 * @author kevin
 */
public class ClassUtil
{
	private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);

	/**
	 * Load the specified class.
	 * @param className The name of the class to load.
	 * @param caller The class of the caller.
	 * @return The specified class.
	 * @throws ClassNotFoundException If the class cannot be found.
	 */
	public static Class forName(final String className, final Class caller)
	throws ClassNotFoundException
	{
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
		if (threadClassLoader != null)
		{
			try
			{
				return Class.forName(className, true, threadClassLoader) ;
			}
			catch (final ClassNotFoundException cnfe)
			{
				if (cnfe.getException() != null)
				{
					throw cnfe ;
				}
			}
		}


		final ClassLoader classLoader = caller.getClassLoader() ;
		if (classLoader != null)
		{
			try
			{
				return Class.forName(className, true, classLoader) ;
			}
			catch (final ClassNotFoundException cnfe)
			{
				if (cnfe.getException() != null)
				{
					throw cnfe ;
				}
			}
		}

		return Class.forName(className, true, ClassLoader.getSystemClassLoader()) ;
	}

	/**
	 * Resolve a proxy for the specified interfaces.
	 * @param interfaces The interfaces associated with the proxy.
	 * @param caller The class of the caller.
	 * @return The specified proxy class.
	 * @throws ClassNotFoundException If the class cannot be found.
	 */
	public static Class resolveProxy(final String[] interfaces, final Class caller)
	throws ClassNotFoundException
	{
		final int numInterfaces = (interfaces == null ? 0 : interfaces.length) ;
		if (numInterfaces == 0)
		{
			throw new ClassNotFoundException("Cannot generate proxy with no interfaces") ;
		}

		final Class[] interfaceClasses = new Class[numInterfaces] ;
		for(int count = 0 ; count < numInterfaces ; count++)
		{
			interfaceClasses[count] = forName(interfaces[count], caller) ;
		}

		final ClassLoader proxyClassLoader ;
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
		if (threadClassLoader != null)
		{
			proxyClassLoader = threadClassLoader ;
		}
		else
		{
			final ClassLoader classLoader = caller.getClassLoader() ;
			if (classLoader != null)
			{
				proxyClassLoader = classLoader ;
			}
			else
			{
				proxyClassLoader = ClassLoader.getSystemClassLoader() ;
			}
		}

		return Proxy.getProxyClass(proxyClassLoader, interfaceClasses) ;
	}

	/**
	 * Get the specified resource as a stream.
	 * @param resourceName The name of the class to load.
	 * @param caller The class of the caller.
	 * @return The input stream for the resource or null if not found.
	 */
	public static InputStream getResourceAsStream(final String resourceName, final Class caller)
	{
		final String resource ;
		if (resourceName.startsWith("/"))
		{
			resource = resourceName.substring(1) ;
		}
		else
		{
			final Package callerPackage = caller.getPackage() ;
			if (callerPackage != null)
			{
				resource = callerPackage.getName().replace('.', '/') + '/' + resourceName ;
			}
			else
			{
				resource = resourceName ;
			}
		}
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
		if (threadClassLoader != null)
		{
			final InputStream is = threadClassLoader.getResourceAsStream(resource) ;
			if (is != null)
			{
				return is ;
			}
		}

		final ClassLoader classLoader = caller.getClassLoader() ;
		if (classLoader != null)
		{
			final InputStream is = classLoader.getResourceAsStream(resource) ;
			if (is != null)
			{
				return is ;
			}
		}

		return ClassLoader.getSystemResourceAsStream(resource) ;
	}

	public static URL getResource(final String resourceName, final Class<?> caller)
	{
		final String resource ;
		if (resourceName.startsWith("/"))
		{
			resource = resourceName.substring(1) ;
		}
		else
		{
			final Package callerPackage = caller.getPackage() ;
			if (callerPackage != null)
			{
				resource = callerPackage.getName().replace('.', '/') + '/' + resourceName ;
			}
			else
			{
				resource = resourceName ;
			}
		}
		final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader() ;
		if (threadClassLoader != null)
		{
			final URL url = threadClassLoader.getResource(resource) ;
			if (url != null)
			{
				return url ;
			}
		}

		final ClassLoader classLoader = caller.getClassLoader() ;
		if (classLoader != null)
		{
			final URL url = classLoader.getResource(resource) ;
			if (url != null)
			{
				return url ;
			}
		}

		return ClassLoader.getSystemResource(resource) ;
	}


	public static List<URL> getResources(String resourcePath, Class<?> caller) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if(resourcePath.startsWith("/")) {
			resourcePath = resourcePath.substring(1);
		}

		if (classLoader != null) {
			return toList(classLoader.getResources(resourcePath));
		}

		classLoader = caller.getClassLoader();
		if (classLoader != null) {
			return toList(classLoader.getResources(resourcePath));
		}

		return new ArrayList<URL>();
	}

	private static <T> List<T> toList(Enumeration<T> objects) {
		List<T> theList = new ArrayList<T>();
		while(objects.hasMoreElements()) {
			theList.add(objects.nextElement());
		}        
		return theList;
	}

	/**
	 * Get a package name and convert it to a path value, so it can be used
	 * in calls to methods like {@link #getResourceAsStream}.
	 * <p/>
	 * Adds a '/' prefix and converts all '." characters to '/'.  Doesn't add a
	 * trailing slash.
	 *
	 * @param packageObj The package.
	 * @return The package path.
	 */
	public static String getPath(Package packageObj) {
		return "/" + packageObj.getName().replace('.', '/');
	}

	public static List<String> getResourceList(String regex, Class caller) {
		ClasspathResourceFilter filter = new ClasspathResourceFilter(regex);
		ClassLoader classLoader;

		classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader instanceof URLClassLoader) {
			filter.filter((URLClassLoader) classLoader);
		}
		classLoader = caller.getClassLoader();
		if(classLoader instanceof URLClassLoader) {
			filter.filter((URLClassLoader) classLoader);
		}

		return filter.getResourceList();
	}

	private static class ClasspathResourceFilter {

		private List<String> resourceList = new ArrayList<String>();
		private Pattern pattern;

		private ClasspathResourceFilter(String regex) {
			pattern = Pattern.compile(regex);
		}

		private void filter(URLClassLoader classLoader) {
			URL[] cpUrls = classLoader.getURLs();

			for (int i = 0; i < cpUrls.length; i++) {
				try {
					File file = new File(cpUrls[i].toURI());
					if(file.isDirectory()) {
						searchClasspathDirTree(file, "");
					} else {
						searchArchive(file);
					}
				} catch (URISyntaxException e) {
					logger.warn("Error searching classpath resource URL '" + cpUrls[i] + "' for resource '" + pattern.pattern() + "': " + e.getMessage());
				} catch (IOException e) {
					logger.warn("Error searching classpath resource URL '" + cpUrls[i] + "' for resource '" + pattern.pattern() + "': " + e.getMessage());
				}
			}
		}

		private void searchClasspathDirTree(File rootDir, String subDir) {
			File currentDir = new File(rootDir, subDir);
			File[] contents = currentDir.listFiles();

			for(File file: contents) {
				if(file.isDirectory()) {
					String subSubDir = subDir + "/" + file.getName();
					searchClasspathDirTree(rootDir, subSubDir);
				} else {
					String resClasspathPath = file.toURI().toString().substring(rootDir.toURI().toString().length() - 1);
					if(isToBeAdded(resClasspathPath)) {
						resourceList.add(resClasspathPath);
					}
				}
			}
		}

		private void searchArchive(File archiveFile) throws IOException {
			ZipFile zip = new ZipFile(archiveFile);
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String resClasspathPath = "/" + entry.getName();
				if(isToBeAdded(resClasspathPath)) {
					resourceList.add(resClasspathPath);
				}
			}
		}

		private boolean isToBeAdded(String resClasspathPath) {
			if(resourceList.contains(resClasspathPath)) {
				// Already in the list e.g. same resource in different archives...
				return false;
			}

			Matcher matcher = pattern.matcher(resClasspathPath);
			return matcher.matches();
		}

		private List<String> getResourceList() {
			return resourceList;
		}
	}
}

