package org.drools.util;

import java.util.ArrayList;
import java.util.List;

public class ClassLoaderUtil {
    public static ClassLoader getClassLoader(final ClassLoader classLoader, Class cls) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader currentClassLoader = cls.getClassLoader();
        List<ClassLoader> list = new ArrayList<ClassLoader>();
        if ( classLoader != null) {
            list.add( classLoader );
        }
        
        if ( contextClassLoader != null && contextClassLoader != classLoader ) {
            list.add( contextClassLoader );
        }
        
        if ( currentClassLoader != null && ( currentClassLoader != classLoader || currentClassLoader != contextClassLoader ) ) {
            list.add( currentClassLoader );
        }
        
        if ( list.size() > 0 ) {
            CompositeClassLoader cl = new CompositeClassLoader( null );
            for ( ClassLoader entry : list ) {
                cl.addClassLoader( entry );
            }
            
            return cl;
            
        } else {
            return list.get(0);
        }
               
    } 
}
