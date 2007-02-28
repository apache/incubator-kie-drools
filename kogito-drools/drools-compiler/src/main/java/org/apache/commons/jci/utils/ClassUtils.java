package org.apache.commons.jci.utils;

import java.io.File;


public final class ClassUtils {

	/**
	 * Please do not use - internal
	 * org/my/Class.xxx -> org.my.Class
	 */
	public static String convertResourceToClassName( final String pResourceName ) {
		return ClassUtils.stripExtension(pResourceName).replace('/', '.');
	}

	/**
	 * Please do not use - internal
	 * org.my.Class -> org/my/Class.class
	 */
	public static String convertClassToResourcePath( final String pName ) {
		return pName.replace('.', '/') + ".class";
	}

	/**
	 * Please do not use - internal
	 * org/my/Class.xxx -> org/my/Class
	 */
	public static String stripExtension( final String pResourceName ) {
		final int i = pResourceName.lastIndexOf('.');
		final String withoutExtension = pResourceName.substring(0, i);
		return withoutExtension;
	}

	public static String toJavaCasing(final String pName) {
	    final char[] name = pName.toLowerCase().toCharArray();
	    name[0] = Character.toUpperCase(name[0]);
	    return new String(name);
	}

	public static String clazzName( final File base, final File file ) {
	    final int rootLength = base.getAbsolutePath().length();
	    final String absFileName = file.getAbsolutePath();
	    final int p = absFileName.lastIndexOf('.');
	    final String relFileName = absFileName.substring(rootLength + 1, p);
	    final String clazzName = relFileName.replace(File.separatorChar, '.');
	    return clazzName;
	}

	public static String relative( final File base, final File file ) {
	    final int rootLength = base.getAbsolutePath().length();
	    final String absFileName = file.getAbsolutePath();
	    final String relFileName = absFileName.substring(rootLength + 1);
		return relFileName;
	}
	
}
