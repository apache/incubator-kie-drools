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
        
        if (classLoader != null ) {
            // the user specified classloader
        	cl.addClassLoader(classLoader); 
        }
        
        if (currentClassLoader != null ) {
            // the current classloader, typically from a drools-core or drools-compiler class
            cl.addClassLoader(currentClassLoader); 
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
