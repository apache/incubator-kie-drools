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
        	cl.addClassLoader(classLoader); 
        }
        if (contextClassLoader != null ) {
        	cl.addClassLoader(contextClassLoader); 
        }     
        if (currentClassLoader != null ) {
        	cl.addClassLoader(currentClassLoader); 
        }   
        if (systemClassLoader != null ) {
        	cl.addClassLoader(systemClassLoader); 
        }        
        
        return cl;
                       
    } 
}
