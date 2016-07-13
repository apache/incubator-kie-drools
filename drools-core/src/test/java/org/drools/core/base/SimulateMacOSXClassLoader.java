package org.drools.core.base;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateMacOSXClassLoader extends ClassLoader {
	private ClassLoader wrappedRealClassLoader;
	private Set<Class<?>> forClasses = Collections.emptySet();

	/**
	 * JVM classloader can't "scan" for available classes, hence class in scope of Mac/OSX simulation of this classloader must be added manually.
	 * @param wrappedRealClassLoader a real classloader wrapped by this simulator.
	 * @param forClasses collection of classes to be added in scope of this simulation.
	 */
	public SimulateMacOSXClassLoader(final ClassLoader wrappedRealClassLoader,
									 final Set<Class<?>> forClasses) {
		this.wrappedRealClassLoader = wrappedRealClassLoader;
		this.forClasses = forClasses;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> macOSXSimilar = null;
		
		// I know Java 8 API would be better but what about potential back-porting to 6.x branch?
		Iterator<Class<?>> iterator = forClasses.iterator();
	    while(iterator.hasNext()) {
	    	Class<?> curElement = iterator.next();
	        if (curElement.getName().equalsIgnoreCase(name)) {
	        	macOSXSimilar = curElement;
	        }
	    }
	    
	    Class<?> loaded = null;
	    if (macOSXSimilar != null) {
	    	loaded = wrappedRealClassLoader.loadClass(macOSXSimilar.getName());
	    } else {
	    	// not in scope of the Mac/OSX simulation, I pass down to the real classloader for the resolution directly.
	    	loaded = wrappedRealClassLoader.loadClass(name);
	    }
		
	    // complete Mac/OSX simulation by checking the name is the one requested.
	    if (loaded.getName().equals(name)) {
	    	return loaded;
	    } else {
	    	String resolvedPackage = loaded.getPackage().getName();
	    	String declaringClasses = "";
	    	Class<?> p = loaded.getDeclaringClass();
	    	while ( p != null ) {
	    		declaringClasses = p.getSimpleName() + "$" + declaringClasses;
	    		p = p.getDeclaringClass();
	    	}
	    	throw new NoClassDefFoundError( resolvedPackage.replace(".", "/") + "/" + name.replace(resolvedPackage+".", "")
	    			+ " (wrong name: " 
	    			+ resolvedPackage.replace(".", "/") + "/" + declaringClasses + loaded.getSimpleName()
	    			+ ")" 
	    			);
	    }
		
	}

	
	/**
	 * JVM classloader can't "scan" for available classes, hence class in scope of Mac/OSX simulation of this classloader must be added manually.
	 * @param clazz class to be added in scope of this simulation.
	 */
	public void addClassInScope(Class<?> clazz) {
		if (this.forClasses == Collections.EMPTY_SET) {
			this.forClasses = new HashSet<Class<?>>();
		}
		this.forClasses.add(clazz);
	}
	
}
