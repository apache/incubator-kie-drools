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
        
        IdentityHashMap<ClassLoader, Object> map = new IdentityHashMap<ClassLoader, Object>();
        map.put( classLoader, null );
        map.put( contextClassLoader, null );
        map.put( currentClassLoader, null );
        map.put( systemClassLoader, null );
        
        if ( map.size() > 0 ) {
            CompositeClassLoader cl = new CompositeClassLoader( null );
            for ( ClassLoader entry : map.keySet() ) {
                if ( entry != null ) {
                    cl.addClassLoader( entry );
                }
            }
            
            return cl;
            
        } else {
            return map.keySet().iterator().next();
        }
               
    } 
}
