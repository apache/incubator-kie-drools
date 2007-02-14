package org.drools.testing.core.utils;

import java.util.List;

import org.drools.semantics.java.ClassTypeResolver;

public final class ObjectUtils {
	
	public static Object createObject(String className) {
	      Object object = null;
	      try {
	    	  //Class classDefinition = Class.forName(className, false, ClassLoader.getSystemClassLoader());
	    	  Class classDefinition = Class.forName(className);
	          object = classDefinition.newInstance();
	      } catch (Exception e) {
	          System.out.println(e);
	      }
	      return object;
	}
	
	public static Class getClassDefn (String className) {
		Class classDefinition = null;
		try {
			//classDefinition = Class.forName(className, false, ClassLoader.getSystemClassLoader());
	        classDefinition = Class.forName(className);
	      } catch (Exception e) {
	          System.out.println(e);
	      }
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
