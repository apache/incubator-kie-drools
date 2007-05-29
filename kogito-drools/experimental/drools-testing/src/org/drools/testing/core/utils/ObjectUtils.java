package org.drools.testing.core.utils;

import java.util.List;

import org.drools.semantics.java.ClassTypeResolver;

public final class ObjectUtils {
	
	public static Object createObject(String className) throws Exception {
	      Object object = null;
	        //Class classDefinition = Class.forName(className, false, ClassLoader.getSystemClassLoader());
	    	  Class classDefinition = Class.forName(className);
	          object = classDefinition.newInstance();
	      
	      return object;
	}
	
	public static Class getClassDefn (String className) throws ClassNotFoundException {
		Class classDefinition = null;
			//classDefinition = Class.forName(className, false, ClassLoader.getSystemClassLoader());
	    classDefinition = Class.forName(className);
	    return classDefinition;
	}
	
	public static Class getClassDefn (String className, List imports, ClassLoader classLoader) {
		Class classDefinition = null;
		try {
			ClassTypeResolver cResolver;
			if (classLoader == null)
				cResolver = new ClassTypeResolver(imports);
			else
				cResolver = new ClassTypeResolver(imports, classLoader);
			classDefinition = cResolver.resolveType(className);
		}catch (Exception e) {
			System.out.println(e);
		}
		return classDefinition;
	}
	
}
