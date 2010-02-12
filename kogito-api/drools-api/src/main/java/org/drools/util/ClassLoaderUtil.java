package org.drools.util;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;

public class ClassLoaderUtil {
    public static ClassLoader getClassLoader(final ClassLoader classLoader, Class cls) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader currentClassLoader = ( cls != null ) ? cls.getClassLoader() : ClassLoaderUtil.class.getClassLoader();
        ClassLoader systemClassLoader = Class.class.getClassLoader().getSystemClassLoader();
        
        CompositeClassLoader cl = new CompositeClassLoader( null );
        
        if (currentClassLoader != null ) {
            // this must come first, so that generated classes use the same classloader for search and execution
            // as the main drools jars (core and compiler)
            cl.addClassLoader(currentClassLoader); 
        } 
        
        if (classLoader != null ) {
            // the user specified classloader
        	cl.addClassLoader(classLoader); 
        }
        
        if (contextClassLoader != null ) {
            // context classloader
        	cl.addClassLoader(contextClassLoader); 
        }       
        
        if (systemClassLoader != null ) {
            // system classloader
        	cl.addClassLoader(systemClassLoader); 
        }        
        
        return cl;
                       
    } 
}
